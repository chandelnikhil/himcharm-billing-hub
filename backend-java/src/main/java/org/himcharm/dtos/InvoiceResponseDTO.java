package org.himcharm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.himcharm.enums.PaymentMode;
import org.himcharm.enums.WhatsAppStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponseDTO {

    private Long id;
    private String invoiceNumber;
    private Long storeId;
    private Long customerId;
    private LocalDateTime invoiceDate;
    private Double subtotal;
    private Double totalAmount;
    private PaymentMode paymentMode;
    private WhatsAppStatus whatsappStatus;
    private LocalDateTime paidAt;
    private List<InvoiceItemResponseDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
