package org.himcharm.dtos;

public record WhatsAppInvoiceResponseDTO(
        String googleReviewUrl,
        String customerPhoneNumber,
        Long customerId
) {
}
