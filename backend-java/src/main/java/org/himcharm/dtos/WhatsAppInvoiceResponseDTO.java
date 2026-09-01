package org.himcharm.dtos;

public record WhatsAppInvoiceResponseDTO(
        String googleReviewUrl,
        String customerPhoneNumber,
        Long customerId,
        InvoiceResponseDTO invoice,
        StoreResponseDTO store,
        CustomerResponseDTO customerProfile
) {
}
