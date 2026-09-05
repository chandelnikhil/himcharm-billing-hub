package org.himcharm.dtos;

public record CustomerActivityResponseDTO(
        long totalCustomers,
        long activeInThreeMonths,
        long dormantThreeToSixMonths,
        long dormantSixToTwelveMonths,
        long dormantTwelvePlusMonths
) {
}
