package org.himcharm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.himcharm.enums.MaritalStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private LocalDate dateOfBirth;
    private MaritalStatus maritalStatus;
    private String spouseName;
    private LocalDate anniversaryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
