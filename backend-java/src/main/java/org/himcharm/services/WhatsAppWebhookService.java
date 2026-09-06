package org.himcharm.services;

import lombok.extern.slf4j.Slf4j;
import org.himcharm.entities.WhatsAppMessage;
import org.himcharm.enums.WhatsAppMessageStatus;
import org.himcharm.repositories.WhatsAppMessageRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Slf4j
@Service
public class WhatsAppWebhookService {

    private final WhatsAppMessageRepository messageRepository;
    private final ObjectMapper objectMapper;
    private final Clock applicationClock;

    public WhatsAppWebhookService(
            WhatsAppMessageRepository messageRepository,
            ObjectMapper objectMapper,
            Clock applicationClock
    ) {
        this.messageRepository = messageRepository;
        this.objectMapper = objectMapper;
        this.applicationClock = applicationClock;
    }

    @Async("whatsAppWebhookTaskExecutor")
    @Transactional
    public void updateMessageStatuses(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            for (JsonNode entry : root.path("entry")) {
                for (JsonNode change : entry.path("changes")) {
                    for (JsonNode statusNode : change.path("value").path("statuses")) {
                        updateMessageStatus(statusNode);
                    }
                }
            }
        } catch (Exception exception) {
            log.error("Unable to process WhatsApp status webhook", exception);
        }
    }

    private void updateMessageStatus(JsonNode statusNode) {
        String whatsAppMessageId = statusNode.path("id").asText();
        String rawStatus = statusNode.path("status").asText();
        if (whatsAppMessageId.isBlank() || rawStatus.isBlank()) {
            log.warn("Ignoring WhatsApp status webhook without message id or status");
            return;
        }

        WhatsAppMessageStatus incomingStatus;
        try {
            incomingStatus = WhatsAppMessageStatus.valueOf(rawStatus.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            log.debug("Ignoring unsupported WhatsApp status '{}' for message {}", rawStatus, whatsAppMessageId);
            return;
        }

        messageRepository.findByWhatsAppMessageId(whatsAppMessageId)
                .ifPresentOrElse(
                        message -> applyStatus(message, incomingStatus, statusNode),
                        () -> log.warn("No WhatsApp message found for webhook message id {}", whatsAppMessageId)
                );
    }

    private void applyStatus(
            WhatsAppMessage message,
            WhatsAppMessageStatus incomingStatus,
            JsonNode statusNode
    ) {
        LocalDateTime eventTime = eventTime(statusNode.path("timestamp").asText());

        switch (incomingStatus) {
            case SENT -> message.setSentAt(eventTime);
            case DELIVERED -> message.setDeliveredAt(eventTime);
            case READ -> message.setReadAt(eventTime);
            case FAILED -> {
                message.setFailedAt(eventTime);
                applyFailureDetails(message, statusNode.path("errors"));
            }
        }

        if (canAdvance(message.getStatus(), incomingStatus)) {
            message.setStatus(incomingStatus);
        }
        messageRepository.save(message);
        log.info("Processed WhatsApp {} status for message {}", incomingStatus, message.getWhatsAppMessageId());
    }

    private boolean canAdvance(
            WhatsAppMessageStatus currentStatus,
            WhatsAppMessageStatus incomingStatus
    ) {
        if (currentStatus == null) {
            return true;
        }
        if (incomingStatus == WhatsAppMessageStatus.FAILED) {
            return currentStatus == WhatsAppMessageStatus.SENT;
        }
        if (currentStatus == WhatsAppMessageStatus.FAILED) {
            return false;
        }
        return statusRank(incomingStatus) >= statusRank(currentStatus);
    }

    private int statusRank(WhatsAppMessageStatus status) {
        return switch (status) {
            case SENT -> 1;
            case DELIVERED -> 2;
            case READ -> 3;
            case FAILED -> 0;
        };
    }

    private void applyFailureDetails(WhatsAppMessage message, JsonNode errors) {
        if (!errors.isArray() || errors.isEmpty()) {
            return;
        }

        JsonNode error = errors.get(0);
        message.setErrorCode(error.path("code").asText("UNKNOWN"));
        String errorMessage = error.path("message").asText();
        if (errorMessage.isBlank()) {
            errorMessage = error.path("title").asText("Unknown WhatsApp error");
        }
        message.setErrorMessage(errorMessage);
    }

    private LocalDateTime eventTime(String epochSeconds) {
        try {
            return LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(Long.parseLong(epochSeconds)),
                    applicationClock.getZone()
            );
        } catch (NumberFormatException | DateTimeParseException exception) {
            return LocalDateTime.now(applicationClock);
        }
    }
}
