package org.himcharm.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequestDTO {

    @NotBlank(message = "Store code is required")
    @Size(max = 30, message = "Store code must not exceed 30 characters")
    private String storeCode;

    @NotBlank(message = "Store name is required")
    @Size(max = 150, message = "Store name must not exceed 150 characters")
    private String name;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @NotBlank(message = "Google review URL is required")
    @Size(max = 1000, message = "Google review URL must not exceed 1000 characters")
    private String googleReviewUrl;

    private Boolean active;
}
