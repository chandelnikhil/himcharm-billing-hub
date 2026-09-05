package org.himcharm.dtos;

import java.time.LocalDateTime;

public record CustomerDashboardResponseDTO(
        CustomerActivityResponseDTO activity,
        CustomerFrequencyResponseDTO frequency,
        long completedProfiles,
        long upcomingBirthdays,
        long upcomingAnniversaries,
        double averageFeedbackRating,
        LocalDateTime generatedAt
) {
}
