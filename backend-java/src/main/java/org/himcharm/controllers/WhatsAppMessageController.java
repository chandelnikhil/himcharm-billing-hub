package org.himcharm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.ApiResponse;
import org.himcharm.whatsapp.WhatsAppService;
import org.himcharm.whatsapp.dto.InvoiceMessageRequest;
import org.himcharm.whatsapp.dto.SendWhatsAppMessageRequest;
import org.himcharm.whatsapp.dto.WhatsAppMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/whatsapp/messages")
@RequiredArgsConstructor
public class WhatsAppMessageController {

    private final WhatsAppService whatsAppService;

    @PostMapping
    public ResponseEntity<ApiResponse> sendTextMessage(
            @Valid @RequestBody SendWhatsAppMessageRequest request
    ) {
        WhatsAppMessageResponse response = whatsAppService.sendTextMessage(
                request.clientPhoneNumber(),
                request.message()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "WhatsApp message submitted successfully",
                        response
                )
        );
    }

    @PostMapping("/invoice")
    public ResponseEntity<ApiResponse> sendInvoiceToCustomer(
            @Valid @RequestBody InvoiceMessageRequest request
    ) {
        WhatsAppMessageResponse response = whatsAppService.sendInvoiceMessage(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Invoice notification sent successfully via WhatsApp",
                        response
                )
        );
    }
}
