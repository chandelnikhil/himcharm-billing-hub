package org.himcharm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResponseDTO {

    private Long id;
    private Long productId;
    private String itemName;
    private Integer quantity;
    private Double unitPrice;
    private Double discountPercentage;
    private Double lineTotal;
}
