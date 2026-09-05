package org.himcharm.services;

import lombok.extern.slf4j.Slf4j;
import org.himcharm.entities.Customer;
import org.himcharm.entities.ManualCampaign;
import org.himcharm.entities.WhatsAppMessage;
import org.himcharm.enums.WhatsAppMessageStatus;
import org.himcharm.enums.WhatsAppMessageType;
import org.himcharm.enums.WhatsAppTemplateCategory;
import org.himcharm.repositories.WhatsAppMessageRepository;
import org.himcharm.whatsapp.WhatsAppClientException;
import org.himcharm.whatsapp.WhatsAppService;
import org.himcharm.whatsapp.dto.WhatsAppMessageResponse;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.function.Function;

@Slf4j
@Service
public class CampaignMessageSender {

    private final WhatsAppMessageRepository messageRepository;
    private final WhatsAppService whatsAppService;
    private final Clock applicationClock;

    public CampaignMessageSender(
            WhatsAppMessageRepository messageRepository,
            WhatsAppService whatsAppService,
            Clock applicationClock
    ) {
        this.messageRepository = messageRepository;
        this.whatsAppService = whatsAppService;
        this.applicationClock = applicationClock;
    }

    public void send(
            Customer customer,
            WhatsAppMessageType messageType,
            String templateName,
            ManualCampaign manualCampaign,
            Function<String, WhatsAppMessageResponse> sendAction
    ) {
        WhatsAppMessage message = WhatsAppMessage.builder()
                .customer(customer)
                .manualCampaign(manualCampaign)
                .messageType(messageType)
                .phoneNumber(customer.getPhone())
                .templateName(templateName)
                .templateCategory(WhatsAppTemplateCategory.MARKETING)
                .templateLanguage(whatsAppService.getInvoiceTemplateLanguage())
                .build();

        try {
            String normalizedPhoneNumber = whatsAppService.normalizePhoneNumber(customer.getPhone());
            message.setPhoneNumber(normalizedPhoneNumber);
            WhatsAppMessageResponse response = sendAction.apply(normalizedPhoneNumber);
            message.setWhatsAppMessageId(firstMessageId(response));
            message.setStatus(WhatsAppMessageStatus.SENT);
            message.setSentAt(LocalDateTime.now(applicationClock));
        } catch (WhatsAppClientException exception) {
            markFailed(message, exception.getErrorCode(), exception.getMessage());
        } catch (RuntimeException exception) {
            markFailed(message, exception.getClass().getSimpleName(), exception.getMessage());
        }

        messageRepository.save(message);
        log.info("{} message {} for customer {}", messageType, message.getStatus(), customer.getName());
    }

    private String firstMessageId(WhatsAppMessageResponse response) {
        if (response.messages() == null || response.messages().isEmpty()) {
            return null;
        }
        return response.messages().getFirst().id();
    }

    private void markFailed(WhatsAppMessage message, String errorCode, String errorMessage) {
        message.setStatus(WhatsAppMessageStatus.FAILED);
        message.setErrorCode(errorCode == null ? "UNKNOWN" : errorCode);
        message.setErrorMessage(errorMessage == null ? "Unknown WhatsApp error" : errorMessage);
        message.setFailedAt(LocalDateTime.now(applicationClock));
    }
}
