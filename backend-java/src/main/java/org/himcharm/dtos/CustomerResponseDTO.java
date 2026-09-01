package org.himcharm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.himcharm.enums.MaritalStatus;
import org.himcharm.enums.Gender;

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
    private Gender gender;
    private MaritalStatus maritalStatus;
    private String spouseName;
    private LocalDate anniversaryDate;
    private LocalDate spouseDateOfBirth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
