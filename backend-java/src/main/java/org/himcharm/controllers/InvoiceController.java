package org.himcharm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.ApiResponse;
import org.himcharm.dtos.InvoiceItemRequestDTO;
import org.himcharm.dtos.InvoiceItemResponseDTO;
import org.himcharm.dtos.InvoiceRequestDTO;
import org.himcharm.dtos.InvoiceResponseDTO;
import org.himcharm.entities.Customer;
import org.himcharm.entities.Invoice;
import org.himcharm.entities.InvoiceItem;
import org.himcharm.entities.Product;
import org.himcharm.entities.Store;
import org.himcharm.services.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ApiResponse> createInvoice(@Valid @RequestBody InvoiceRequestDTO request) {
        Invoice invoice = invoiceService.createInvoice(toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "Invoice created successfully", toResponse(invoice))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllInvoices() {
        List<InvoiceResponseDTO> invoices = invoiceService.getAllInvoices().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Invoices fetched successfully", invoices)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getInvoiceById(@PathVariable Long id) {
        InvoiceResponseDTO invoice = toResponse(invoiceService.getInvoiceById(id));
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Invoice fetched successfully", invoice)
        );
    }

    private Invoice toEntity(InvoiceRequestDTO request) {
        Invoice invoice = Invoice.builder()
                .store(Store.builder().id(request.getStoreId()).build())
                .customer(Customer.builder().phone(request.getCustomerPhoneNumber()).build())
                .invoiceDate(request.getInvoiceDate())
                .paymentMode(request.getPaymentMode())
                .paidAt(request.getPaidAt())
                .build();

        for (InvoiceItemRequestDTO itemRequest : request.getItems()) {
            Product product = itemRequest.getProductId() == null
                    ? null
                    : Product.builder().id(itemRequest.getProductId()).build();
            invoice.addItem(InvoiceItem.builder()
                    .product(product)
                    .itemName(itemRequest.getItemName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(itemRequest.getUnitPrice())
                    .discountPercentage(itemRequest.getDiscountPercentage())
                    .build());
        }
        return invoice;
    }

    private InvoiceResponseDTO toResponse(Invoice invoice) {
        List<InvoiceItemResponseDTO> items = invoice.getItems().stream()
                .map(item -> InvoiceItemResponseDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct() == null ? null : item.getProduct().getId())
                        .itemName(item.getItemName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .discountPercentage(item.getDiscountPercentage())
                        .lineTotal(item.getLineTotal())
                        .build())
                .toList();

        return InvoiceResponseDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .storeId(invoice.getStore().getId())
                .customerId(invoice.getCustomer().getId())
                .invoiceDate(invoice.getInvoiceDate())
                .subtotal(invoice.getSubtotal())
                .totalAmount(invoice.getTotalAmount())
                .paymentMode(invoice.getPaymentMode())
                .whatsappStatus(invoice.getWhatsappStatus())
                .paidAt(invoice.getPaidAt())
                .items(items)
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }
}
