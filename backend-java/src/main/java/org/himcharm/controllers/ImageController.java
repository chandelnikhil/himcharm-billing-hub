package org.himcharm.controllers;

import org.himcharm.dtos.ApiResponse;
import org.himcharm.dtos.CampaignImageResponse;
import org.himcharm.enums.CampaignImageType;
import org.himcharm.services.CampaignImageService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final CampaignImageService campaignImageService;

    public ImageController(CampaignImageService campaignImageService) {
        this.campaignImageService = campaignImageService;
    }

    @GetMapping("/{type}")
    public ResponseEntity<?> getImage(@PathVariable String type) {
        CampaignImageType imageType = CampaignImageType.fromPath(type);
        CampaignImageService.ImageResource image = campaignImageService.getImage(imageType);

        return ResponseEntity.ok()
                .contentType(image.mediaType())
                .cacheControl(CacheControl.noStore())
                .body(image.resource());
    }

    @GetMapping("/{type}/{fileName}")
    public ResponseEntity<?> getUploadedImage(
            @PathVariable String type,
            @PathVariable String fileName
    ) {
        CampaignImageType imageType = CampaignImageType.fromPath(type);
        CampaignImageService.ImageResource image = campaignImageService.getUploadedImage(imageType, fileName);

        return ResponseEntity.ok()
                .contentType(image.mediaType())
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
                .body(image.resource());
    }

    @PostMapping(value = "/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadImage(
            @PathVariable String type,
            @RequestParam("file") MultipartFile file
    ) {
        CampaignImageType imageType = CampaignImageType.fromPath(type);
        String fileName = campaignImageService.store(imageType, file);
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(imageType.getPathValue())
                .path("/")
                .path(fileName)
                .build()
                .toUriString();

        CampaignImageResponse response = new CampaignImageResponse(
                imageType.getPathValue(),
                fileName,
                imageUrl
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "Campaign image uploaded", response)
        );
    }
}
