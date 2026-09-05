package org.himcharm.dtos;

import lombok.Builder;
import org.himcharm.enums.WhatsAppMessageStatus;
import org.himcharm.enums.WhatsAppMessageType;

import java.time.LocalDateTime;

@Builder
public record ManualCampaignMessageResponseDTO(
        Long id,
        Long campaignId,
        String customerName,
        String customerPhoneNumber,
        WhatsAppMessageType messageType,
        WhatsAppMessageStatus messageStatus,
        String failureReason,
        LocalDateTime failedAt,
        LocalDateTime sentAt,
        LocalDateTime createdAt
) {
}
