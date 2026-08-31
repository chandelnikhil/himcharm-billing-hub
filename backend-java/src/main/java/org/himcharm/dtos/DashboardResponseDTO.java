package org.himcharm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {

    private LocalDate fromDate;
    private LocalDate toDate;
    private Long storeId;
    private DashboardMetricsResponseDTO metrics;
    private List<DashboardTrendPointResponseDTO> trend;
    private LocalDateTime generatedAt;
}
