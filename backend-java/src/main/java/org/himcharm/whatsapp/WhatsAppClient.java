package org.himcharm.whatsapp;

import org.himcharm.utilies.Constants;
import org.himcharm.whatsapp.dto.WhatsAppMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Component
public class WhatsAppClient {

    private static final String MESSAGE_PATH = "/{senderPhoneNumberId}/messages";

    private final RestClient restClient;
    private final String senderPhoneNumberId;
    private final ObjectMapper objectMapper;

    public WhatsAppClient(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${whatsapp.api-base-url:https://graph.facebook.com/v23.0}") String apiBaseUrl,
            @Value("${whatsapp.phone-number-id}") String senderPhoneNumberId,
            @Value("${whatsapp.access-token}") String accessToken
    ) {
        this.senderPhoneNumberId = senderPhoneNumberId;
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder
                .baseUrl(apiBaseUrl)
                .defaultHeaders(headers -> headers.setBearerAuth(accessToken))
                .build();
    }

    public WhatsAppMessageResponse sendTextMessage(String recipientPhoneNumber, String message) {
        Map<String, Object> request = Map.of(
                "messaging_product", "whatsapp",
                "recipient_type", "individual",
                "to", recipientPhoneNumber,
                "type", "text",
                "text", Map.of("preview_url", false, "body", message)
        );

        try {
            WhatsAppMessageResponse response = restClient.post()
                    .uri(MESSAGE_PATH, senderPhoneNumberId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(WhatsAppMessageResponse.class);

            if (response == null) {
                throw new WhatsAppClientException(
                        "EMPTY_RESPONSE",
                        "WhatsApp returned an empty response",
                        null
                );
            }
            return response;
        } catch (RestClientResponseException exception) {
            throw toWhatsAppClientException(exception, "WhatsApp rejected the message");
        } catch (ResourceAccessException exception) {
            throw new WhatsAppClientException(
                    "NETWORK_ERROR",
                    "Unable to reach the WhatsApp API",
                    exception
            );
        }
    }

    public WhatsAppMessageResponse sendTemplateMessage(
            String recipientPhoneNumber,
            String templateName,
            String languageCode,
            List<String> bodyParameters
    ) {
        Map<String, Object> request = Map.of(
                "messaging_product", "whatsapp",
                "to", Constants.COUNTRY_CODE + recipientPhoneNumber,
                "type", "template",
                "template", Map.of(
                        "name", templateName,
                        "language", Map.of("code", languageCode),
                        "components", List.of(
                                Map.of(
                                        "type", "body",
                                        "parameters", bodyParameters.stream()
                                                .map(text -> Map.of("type", "text", "text", text))
                                                .toList()
                                )
                        )
                )
        );

        try {
            WhatsAppMessageResponse response = restClient.post()
                    .uri(MESSAGE_PATH, senderPhoneNumberId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(WhatsAppMessageResponse.class);

            if (response == null) {
                throw new WhatsAppClientException(
                        "EMPTY_RESPONSE",
                        "WhatsApp returned an empty response",
                        null
                );
            }
            return response;
        } catch (RestClientResponseException exception) {
            throw toWhatsAppClientException(exception, "WhatsApp rejected the template message");
        } catch (ResourceAccessException exception) {
            throw new WhatsAppClientException(
                    "NETWORK_ERROR",
                    "Unable to reach the WhatsApp API",
                    exception
            );
        }
    }

    private WhatsAppClientException toWhatsAppClientException(
            RestClientResponseException exception,
            String fallbackMessage
    ) {
        String errorCode = "HTTP_" + exception.getStatusCode().value();
        String errorMessage = fallbackMessage + " with HTTP status " + exception.getStatusCode().value();

        try {
            JsonNode error = objectMapper.readTree(exception.getResponseBodyAsString()).path("error");
            if (!error.isMissingNode()) {
                errorCode = error.path("code").asText(errorCode);
                errorMessage = error.path("message").asText(errorMessage);
            }
        } catch (Exception ignored) {
            // Keep the HTTP status fallback when Meta does not return valid JSON.
        }

        return new WhatsAppClientException(errorCode, errorMessage, exception);
    }
}
