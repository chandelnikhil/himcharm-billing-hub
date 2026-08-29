package org.himcharm.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.himcharm.enums.PaymentMode;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class InvoiceRequestDTO {

    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotBlank(message = "Customer phone number is required")
    @Size(max = 20, message = "Customer phone number must not exceed 20 characters")
    private String customerPhoneNumber;

    private LocalDateTime invoiceDate;

    private PaymentMode paymentMode;

    private LocalDateTime paidAt;

    @Valid
    @NotEmpty(message = "At least one invoice item is required")
    private List<InvoiceItemRequestDTO> items;
}
