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
public class DashboardMetricsResponseDTO {

    private Double revenue;
    private Long bills;
    private Double averageOrderValue;
    private Double newRevenue;
    private Double repeatRevenue;
    private Long newBills;
    private Long repeatBills;
    private Double newAverageOrderValue;
    private Double repeatAverageOrderValue;
}
