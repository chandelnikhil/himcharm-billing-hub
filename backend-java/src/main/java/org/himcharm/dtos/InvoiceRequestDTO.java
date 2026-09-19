package org.himcharm.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.himcharm.enums.PaymentMode;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceRequestDTO {

    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotBlank(message = "Customer phone number is required")
    @Size(max = 20, message = "Customer phone number must not exceed 20 characters")
    private String customerPhoneNumber;

    @Size(max = 150, message = "Customer name must not exceed 150 characters")
    private String customerName;

    @NotNull(message = "Customer date of birth is required")
    @PastOrPresent(message = "Customer date of birth cannot be in the future")
    private LocalDate customerDateOfBirth;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;

    @Valid
    @NotEmpty(message = "At least one invoice item is required")
    private List<InvoiceItemRequestDTO> items;
}
