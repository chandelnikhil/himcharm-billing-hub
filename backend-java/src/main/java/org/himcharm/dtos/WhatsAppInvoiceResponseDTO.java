package org.himcharm.dtos;

public record WhatsAppInvoiceResponseDTO(
        String googleReviewUrl,
        String customerPhoneNumber,
        Long customerId,
        boolean feedbackSubmitted,
        InvoiceResponseDTO invoice,
        StoreResponseDTO store,
        CustomerResponseDTO customerProfile
) {
}
