package org.himcharm.dtos;

public record FeedbackResponseDTO(
        Long id,
        Integer rating,
        String feedback,
        Long customerId,
        Long storeId
) {
}
