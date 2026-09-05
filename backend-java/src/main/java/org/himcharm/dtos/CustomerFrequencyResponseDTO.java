package org.himcharm.dtos;

public record CustomerFrequencyResponseDTO(
        long oneTimeVisit,
        long twoTimesVisits,
        long threeTimesVisits,
        long fourTimesVisits,
        long fivePlusTimesVisits,
        long tenPlusTimesVisits
) {
}
