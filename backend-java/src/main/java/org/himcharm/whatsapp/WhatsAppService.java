package org.himcharm.whatsapp;

import lombok.Getter;
import org.himcharm.utilies.Base64UrlCodec;
import org.himcharm.whatsapp.dto.InvoiceMessageRequest;
import org.himcharm.whatsapp.dto.WhatsAppMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Service
public class WhatsAppService {

    private static final String PHONE_NUMBER_PATTERN = "[1-9]\\d{7,14}";
    private static final DateTimeFormatter CAMPAIGN_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final WhatsAppClient whatsAppClient;
    private final String invoiceTemplateName;
    private final String birthdayTemplateName;
    private final String anniversaryTemplateName;
    private final String birthdayImageUrl;
    private final String anniversaryImageUrl;
    private final String invoiceTemplateLanguage;

    public WhatsAppService(
            WhatsAppClient whatsAppClient,
            @Value("${whatsapp.template.invoice.name}") String invoiceTemplateName,
            @Value("${whatsapp.template.birthday.name}") String birthdayTemplateName,
            @Value("${whatsapp.template.anniversary.name}") String anniversaryTemplateName,
            @Value("${whatsapp.template.birthday.image-url}") String birthdayImageUrl,
            @Value("${whatsapp.template.anniversary.image-url}") String anniversaryImageUrl,
            @Value("${whatsapp.template.language:en_US}") String invoiceTemplateLanguage
    ) {
        this.whatsAppClient = whatsAppClient;
        this.invoiceTemplateName = invoiceTemplateName;
        this.birthdayTemplateName = birthdayTemplateName;
        this.anniversaryTemplateName = anniversaryTemplateName;
        this.birthdayImageUrl = birthdayImageUrl;
        this.anniversaryImageUrl = anniversaryImageUrl;
        this.invoiceTemplateLanguage = invoiceTemplateLanguage;
    }

    public WhatsAppMessageResponse sendTextMessage(String clientPhoneNumber, String message) {
        String normalizedPhoneNumber = normalizePhoneNumber(clientPhoneNumber);
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("WhatsApp message must not be blank");
        }

        return whatsAppClient.sendTextMessage(normalizedPhoneNumber, message.trim());
    }

    public WhatsAppMessageResponse sendInvoiceMessage(InvoiceMessageRequest request) {
        String normalizedPhoneNumber = normalizePhoneNumber(request.phoneNumber());

        if (request.customerName() == null || request.customerName().isBlank()) {
            throw new IllegalArgumentException("Customer name must not be blank");
        }
        if (request.billNumber() == null || request.billNumber().isBlank()) {
            throw new IllegalArgumentException("Bill number must not be blank");
        }

        List<String> bodyParameters = List.of(
                request.customerName().trim(),
                request.billNumber().trim(),
                String.valueOf(request.amount())
        );

        return whatsAppClient.sendTemplateMessage(
                normalizedPhoneNumber,
                invoiceTemplateName,
                invoiceTemplateLanguage,
                bodyParameters,
                Base64UrlCodec.encode(request.billNumber().trim())
        );
    }

    public WhatsAppMessageResponse sendAutomatedCampaignMessage(
            String customerPhoneNumber,
            String customerName,
            String off,
            LocalDate offerValidUntil,
            String templateName,
            String imageUrl
    ) {
        String normalizedPhoneNumber = normalizePhoneNumber(customerPhoneNumber);

        List<String> bodyParameters = List.of(
                customerName.trim(),
                off,
                offerValidUntil.format(CAMPAIGN_DATE_FORMATTER)
        );

        return whatsAppClient.sendImageHeaderTemplateMessage(
                normalizedPhoneNumber,
                templateName,
                invoiceTemplateLanguage,
                imageUrl,
                bodyParameters
        );
    }

    public String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Client phone number must not be blank");
        }

        String normalized = phoneNumber.trim()
                .replace("+", "")
                .replace(" ", "")
                .replace("-", "");
        if (!normalized.matches(PHONE_NUMBER_PATTERN)) {
            throw new IllegalArgumentException(
                    "Client phone number must include the country code and contain only digits"
            );
        }
        return normalized;
    }
}
