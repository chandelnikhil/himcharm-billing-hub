package org.himcharm.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/webhooks/whatsapp")
public class WhatsAppWebhookController {

    private final String verifyToken;

    public WhatsAppWebhookController(
            @Value("${whatsapp.webhook.verify-token}") String verifyToken
    ) {
        this.verifyToken = verifyToken;
    }

    /**
     * Meta calls this endpoint while registering or changing the webhook URL.
     */
    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode") String mode,
            @RequestParam(name = "hub.challenge") String challenge,
            @RequestParam(name = "hub.verify_token") String suppliedVerifyToken
    ) {
        if ("subscribe".equals(mode) && verifyToken.equals(suppliedVerifyToken)) {
            return ResponseEntity.ok(challenge);
        }

        log.warn("Rejected WhatsApp webhook verification request with mode={}", mode);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Receives WhatsApp events. Payload handling can be added here later.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> receiveWebhook(@RequestBody String payload) {
        log.info("Received WhatsApp webhook payload: {}", payload);
        return ResponseEntity.ok().build();
    }
}

