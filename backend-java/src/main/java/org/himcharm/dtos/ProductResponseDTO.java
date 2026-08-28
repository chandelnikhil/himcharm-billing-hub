package org.himcharm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String brand;
    private BigDecimal defaultPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
