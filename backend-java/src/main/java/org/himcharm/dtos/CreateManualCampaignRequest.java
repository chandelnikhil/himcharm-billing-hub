package org.himcharm.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateManualCampaignRequest(
        @NotBlank(message = "Festival name is required")
        String festivalName,

        @NotNull(message = "Offer percentage is required")
        @DecimalMin(value = "0.01", message = "Offer percentage must be greater than 0")
        @DecimalMax(value = "100", message = "Offer percentage cannot exceed 100")
        BigDecimal offerPercentage,

        @NotNull(message = "Valid up to date is required")
        @FutureOrPresent(message = "Valid up to date cannot be in the past")
        LocalDate validUpTo
) {
}
