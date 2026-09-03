package org.himcharm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.ApiResponse;
import org.himcharm.dtos.AutomatedCampaignMessageRequestDTO;
import org.himcharm.dtos.AutomatedCampaignMessageResponseDTO;
import org.himcharm.dtos.PageResponseDTO;
import org.himcharm.entities.Customer;
import org.himcharm.entities.WhatsAppMessage;
import org.himcharm.enums.WhatsAppMessageType;
import org.himcharm.repositories.WhatsAppMessageRepository;
import org.himcharm.whatsapp.WhatsAppService;
import org.himcharm.whatsapp.dto.InvoiceMessageRequest;
import org.himcharm.whatsapp.dto.SendWhatsAppMessageRequest;
import org.himcharm.whatsapp.dto.WhatsAppMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/whatsapp/messages")
@RequiredArgsConstructor
public class WhatsAppMessageController {

    private static final int PAGE_SIZE = 30;

    private final WhatsAppService whatsAppService;
    private final WhatsAppMessageRepository whatsAppMessageRepository;

    @GetMapping("/automated-campaigns")
    public ResponseEntity<ApiResponse> getAutomatedCampaignMessages(
            @Valid @ModelAttribute AutomatedCampaignMessageRequestDTO request
    ) {
        if (request.getPage() < 0) {
            throw new IllegalStateException("Page number cannot be negative");
        }
        if (request.getFromDate() != null && request.getToDate() != null
                && request.getFromDate().isAfter(request.getToDate())) {
            throw new IllegalStateException("From date cannot be after to date");
        }
        if (request.getCampaignType() != null
                && request.getCampaignType() != WhatsAppMessageType.BIRTHDAY
                && request.getCampaignType() != WhatsAppMessageType.ANNIVERSARY) {
            throw new IllegalStateException("Campaign type must be BIRTHDAY or ANNIVERSARY");
        }

        PageRequest pageable = PageRequest.of(
                request.getPage(),
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<WhatsAppMessage> messagePage = whatsAppMessageRepository.findAutomatedCampaignMessages(
                java.util.List.of(WhatsAppMessageType.BIRTHDAY, WhatsAppMessageType.ANNIVERSARY),
                request.getCampaignType(),
                request.getFromDate() == null ? null : request.getFromDate().atStartOfDay(),
                request.getToDate() == null ? null : request.getToDate().plusDays(1).atStartOfDay(),
                pageable
        );
        PageResponseDTO<AutomatedCampaignMessageResponseDTO> messages = PageResponseDTO
                .<AutomatedCampaignMessageResponseDTO>builder()
                .content(messagePage.getContent().stream().map(this::toAutomatedCampaignResponse).toList())
                .page(messagePage.getNumber())
                .size(messagePage.getSize())
                .totalElements(messagePage.getTotalElements())
                .totalPages(messagePage.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Automated campaign messages fetched successfully", messages)
        );
    }

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

    private AutomatedCampaignMessageResponseDTO toAutomatedCampaignResponse(WhatsAppMessage message) {
        Customer customer = message.getCustomer();
        return AutomatedCampaignMessageResponseDTO.builder()
                .id(message.getId())
                .customerName(customer.getName())
                .customerPhoneNumber(message.getPhoneNumber())
                .customerBirthDate(customer.getDateOfBirth())
                .customerAnniversaryDate(customer.getAnniversaryDate())
                .messageType(message.getMessageType())
                .messageStatus(message.getStatus())
                .failureReason(message.getErrorMessage())
                .failedAt(message.getFailedAt())
                .sentAt(message.getSentAt())
                .build();
    }
}
