package org.himcharm.whatsapp.dto;

import jakarta.validation.constraints.NotBlank;

public record InvoiceMessageRequest(
        @NotBlank(message = "Customer name is required")
        String customerName,

        @NotBlank(message = "Bill number is required")
        String billNumber,

        @NotBlank(message = "Phone number is required")
        String phoneNumber
) {
}
