package org.himcharm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.himcharm.enums.WhatsAppMessageStatus;
import org.himcharm.enums.WhatsAppMessageType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutomatedCampaignMessageResponseDTO {

    private Long id;
    private String customerName;
    private String customerPhoneNumber;
    private LocalDate customerBirthDate;
    private LocalDate customerAnniversaryDate;
    private WhatsAppMessageType messageType;
    private WhatsAppMessageStatus messageStatus;
    private String failureReason;
    private LocalDateTime failedAt;
    private LocalDateTime sentAt;
}
