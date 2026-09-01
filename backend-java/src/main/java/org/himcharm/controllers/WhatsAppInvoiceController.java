package org.himcharm.controllers;

import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.ApiResponse;
import org.himcharm.dtos.WhatsAppInvoiceResponseDTO;
import org.himcharm.entities.Invoice;
import org.himcharm.services.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/whatsapp/invoice")
@RequiredArgsConstructor
public class WhatsAppInvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<ApiResponse> getInvoiceReviewDetails(
            @RequestParam String invoiceNumber
    ) {
        String decodedInvoiceNumber = new String(
                Base64.getDecoder().decode(invoiceNumber),
                StandardCharsets.UTF_8
        );
        Invoice invoice = invoiceService.getInvoiceByInvoiceNumber(decodedInvoiceNumber);
        WhatsAppInvoiceResponseDTO response = new WhatsAppInvoiceResponseDTO(
                invoice.getStore().getGoogleReviewUrl(),
                invoice.getCustomer().getPhone(),
                invoice.getCustomer().getId()
        );

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Invoice review details fetched successfully",
                response
        ));
    }
}
