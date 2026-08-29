package org.himcharm.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.himcharm.enums.MaritalStatus;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerRequestDTO {

    private String name;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    private String email;

    private LocalDate dateOfBirth;

    private MaritalStatus maritalStatus;

    private String spouseName;

    private LocalDate anniversaryDate;
}
