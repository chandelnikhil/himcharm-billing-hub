package org.himcharm.whatsapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendWhatsAppMessageRequest(
        @NotBlank(message = "Client phone number is required")
        @Size(max = 25, message = "Client phone number must not exceed 25 characters")
        String clientPhoneNumber,

        @NotBlank(message = "Message is required")
        @Size(max = 4096, message = "Message must not exceed 4096 characters")
        String message
) {
}
