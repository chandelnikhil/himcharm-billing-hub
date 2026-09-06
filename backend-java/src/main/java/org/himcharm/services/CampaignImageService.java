package org.himcharm.services;

import org.himcharm.enums.CampaignImageType;
import org.himcharm.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class CampaignImageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg");
    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a
    };
    private static final byte[] JPEG_SIGNATURE = {
            (byte) 0xff, (byte) 0xd8, (byte) 0xff
    };

    private final Path storageDirectory;
    private final String publicBaseUrl;
    private final Map<CampaignImageType, String> fallbackUrls;

    public CampaignImageService(
            @Value("${campaign.image.storage-directory}") String storageDirectory,
            @Value("${campaign.image.public-base-url}") String publicBaseUrl,
            @Value("${whatsapp.template.festival.image-url}") String festivalFallbackUrl,
            @Value("${whatsapp.template.birthday.image-url}") String birthdayFallbackUrl,
            @Value("${whatsapp.template.anniversary.image-url}") String anniversaryFallbackUrl
    ) {
        this.storageDirectory = Path.of(storageDirectory).toAbsolutePath().normalize();
        this.publicBaseUrl = stripTrailingSlash(publicBaseUrl);
        this.fallbackUrls = new EnumMap<>(CampaignImageType.class);
        this.fallbackUrls.put(CampaignImageType.FESTIVAL, festivalFallbackUrl);
        this.fallbackUrls.put(CampaignImageType.BIRTHDAY, birthdayFallbackUrl);
        this.fallbackUrls.put(CampaignImageType.ANNIVERSARY, anniversaryFallbackUrl);
    }

    public String store(CampaignImageType type, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a non-empty image file");
        }

        String extension = getExtension(file.getOriginalFilename());
        validateImageSignature(file, extension);

        try {
            Files.createDirectories(storageDirectory);
            String uniqueFileName = type.getFileBaseName() + "-" + UUID.randomUUID() + "." + extension;
            Path target = storageDirectory.resolve(uniqueFileName);
            Path temporaryFile = Files.createTempFile(storageDirectory, type.getFileBaseName() + "-", ".tmp");
            try {
                file.transferTo(temporaryFile);
                moveToFinalName(temporaryFile, target);
                deletePreviousImages(type, target);
            } finally {
                Files.deleteIfExists(temporaryFile);
            }
            return target.getFileName().toString();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not store campaign image", exception);
        }
    }

    public ImageResource getImage(CampaignImageType type) {
        Optional<Path> localImage = findLocalImage(type);
        if (localImage.isPresent()) {
            Path path = localImage.get();
            return new ImageResource(
                    new FileSystemResource(path),
                    mediaType(extensionOf(path.getFileName().toString())),
                    true
            );
        }

        String fallbackUrl = fallbackUrls.get(type);
        try {
            URI uri = URI.create(fallbackUrl);
            if (!Set.of("http", "https").contains(uri.getScheme())) {
                throw new IllegalStateException("Campaign image fallback URL must use HTTP or HTTPS");
            }
            return new ImageResource(
                    new UrlResource(uri),
                    mediaType(extensionOf(uri.getPath())),
                    false
            );
        } catch (IllegalArgumentException | MalformedURLException exception) {
            throw new IllegalStateException("Campaign image fallback URL is invalid", exception);
        }
    }

    public ImageResource getUploadedImage(CampaignImageType type, String fileName) {
        if (!isImageForType(fileName, type)) {
            throw new ResourceNotFoundException("Campaign image not found");
        }

        Path imagePath = storageDirectory.resolve(fileName).normalize();
        if (!imagePath.getParent().equals(storageDirectory) || !Files.isRegularFile(imagePath)) {
            throw new ResourceNotFoundException("Campaign image not found");
        }

        return new ImageResource(
                new FileSystemResource(imagePath),
                mediaType(extensionOf(fileName)),
                true
        );
    }

    public String getCampaignImageUrl(CampaignImageType type) {
        Optional<Path> localImage = findLocalImage(type);
        if (localImage.isPresent()) {
            String fileName = localImage.get().getFileName().toString();
            return publicBaseUrl + "/images/" + type.getPathValue() + "/" + fileName;
        }
        return fallbackUrls.get(type);
    }

    private Optional<Path> findLocalImage(CampaignImageType type) {
        if (!Files.isDirectory(storageDirectory)) {
            return Optional.empty();
        }

        try (Stream<Path> files = Files.list(storageDirectory)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> isImageForType(path.getFileName().toString(), type))
                    .max(Comparator.comparingLong(this::lastModified)
                            .thenComparing(path -> path.getFileName().toString()));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read campaign images", exception);
        }
    }

    private String getExtension(String originalFilename) {
        String extension = extensionOf(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only PNG, JPG, and JPEG images are allowed");
        }
        return extension;
    }

    private String extensionOf(String filename) {
        if (filename == null) {
            return "";
        }
        int queryStart = filename.indexOf('?');
        String cleanFilename = queryStart >= 0 ? filename.substring(0, queryStart) : filename;
        int dotIndex = cleanFilename.lastIndexOf('.');
        return dotIndex < 0 ? "" : cleanFilename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private void validateImageSignature(MultipartFile file, String extension) {
        byte[] expectedSignature = extension.equals("png") ? PNG_SIGNATURE : JPEG_SIGNATURE;
        byte[] actualSignature = new byte[expectedSignature.length];
        try (InputStream inputStream = file.getInputStream()) {
            int bytesRead = inputStream.read(actualSignature);
            if (bytesRead != expectedSignature.length || !Arrays.equals(expectedSignature, actualSignature)) {
                throw new IllegalArgumentException("The selected file is not a valid " + extension.toUpperCase(Locale.ROOT) + " image");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read uploaded image", exception);
        }
    }

    private boolean isImageForType(String fileName, CampaignImageType type) {
        String normalizedFileName = fileName.toLowerCase(Locale.ROOT);
        String baseName = type.getFileBaseName();
        String compatibleBaseNames = type == CampaignImageType.ANNIVERSARY
                ? "(?:anniversary|anniversay)"
                : baseName;
        return ALLOWED_EXTENSIONS.stream()
                .anyMatch(extension -> normalizedFileName.matches(
                        compatibleBaseNames
                                + "(?:-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})?\\."
                                + extension
                ));
    }

    private long lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not inspect campaign image", exception);
        }
    }

    private void moveToFinalName(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target);
        }
    }

    private void deletePreviousImages(CampaignImageType type, Path retainedImage) throws IOException {
        try (Stream<Path> files = Files.list(storageDirectory)) {
            for (Path path : files.toList()) {
                if (!path.equals(retainedImage)
                        && Files.isRegularFile(path)
                        && isImageForType(path.getFileName().toString(), type)) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    private MediaType mediaType(String extension) {
        return extension.equals("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
    }

    private String stripTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    public record ImageResource(Resource resource, MediaType mediaType, boolean uploaded) {
    }
}
