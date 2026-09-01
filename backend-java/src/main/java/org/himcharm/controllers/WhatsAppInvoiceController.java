package org.himcharm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.ApiResponse;
import org.himcharm.dtos.CustomerRequestDTO;
import org.himcharm.dtos.CustomerResponseDTO;
import org.himcharm.dtos.InvoiceItemResponseDTO;
import org.himcharm.dtos.InvoiceResponseDTO;
import org.himcharm.dtos.StoreResponseDTO;
import org.himcharm.dtos.WhatsAppInvoiceResponseDTO;
import org.himcharm.entities.Customer;
import org.himcharm.entities.Invoice;
import org.himcharm.services.CustomerService;
import org.himcharm.services.InvoiceService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/whatsapp/invoice")
@RequiredArgsConstructor
public class WhatsAppInvoiceController {

    private final InvoiceService invoiceService;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<ApiResponse> getInvoiceReviewDetails(
            @RequestParam String invoiceNumber
    ) {
        Invoice invoice = getInvoice(invoiceNumber);
        WhatsAppInvoiceResponseDTO response = toResponse(invoice);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Invoice review details fetched successfully",
                response
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateCustomerProfile(
            @RequestParam String invoiceNumber,
            @Valid @RequestBody CustomerRequestDTO request
    ) {
        Invoice invoice = getInvoice(invoiceNumber);
        Customer currentCustomer = invoice.getCustomer();
        Customer updatedCustomer = modelMapper.map(request, Customer.class);
        updatedCustomer.setPhone(currentCustomer.getPhone());

        Customer savedCustomer = customerService.updateCustomer(currentCustomer.getId(), updatedCustomer);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Customer profile updated successfully",
                modelMapper.map(savedCustomer, CustomerResponseDTO.class)
        ));
    }

    private Invoice getInvoice(String encodedInvoiceNumber) {
        try {
            String decodedInvoiceNumber = new String(
                    Base64.getDecoder().decode(encodedInvoiceNumber),
                    StandardCharsets.UTF_8
            );
            return invoiceService.getInvoiceByInvoiceNumber(decodedInvoiceNumber);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Invalid invoice link");
        }
    }

    private WhatsAppInvoiceResponseDTO toResponse(Invoice invoice) {
        return new WhatsAppInvoiceResponseDTO(
                invoice.getStore().getGoogleReviewUrl(),
                invoice.getCustomer().getPhone(),
                invoice.getCustomer().getId(),
                toInvoiceResponse(invoice),
                modelMapper.map(invoice.getStore(), StoreResponseDTO.class),
                modelMapper.map(invoice.getCustomer(), CustomerResponseDTO.class)
        );
    }

    private InvoiceResponseDTO toInvoiceResponse(Invoice invoice) {
        List<InvoiceItemResponseDTO> items = invoice.getItems().stream()
                .map(item -> {
                    InvoiceItemResponseDTO response = modelMapper.map(item, InvoiceItemResponseDTO.class);
                    response.setProductId(item.getProduct() == null ? null : item.getProduct().getId());
                    return response;
                })
                .toList();

        InvoiceResponseDTO response = modelMapper.map(invoice, InvoiceResponseDTO.class);
        response.setStoreId(invoice.getStore().getId());
        response.setCustomerId(invoice.getCustomer().getId());
        response.setItems(items);
        return response;
    }
}
