package org.himcharm.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Brand is required")
    @Size(max = 150, message = "Brand must not exceed 150 characters")
    private String brand;

    @NotNull(message = "Default price is required")
    @DecimalMin(value = "0.00", message = "Default price cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Default price must have at most 10 integer and 2 decimal digits")
    private BigDecimal defaultPrice;
}
