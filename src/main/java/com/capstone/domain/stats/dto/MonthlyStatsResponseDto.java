package com.capstone.domain.stats.dto;

import java.util.List;

public record MonthlyStatsResponseDto(
        int year,
        int month,
        long journalCount,
        long answerCount,
        String topEmotion,
        List<EmotionStatDto> emotionDistribution
) {
    public record EmotionStatDto(
            String emotion,
            long count,
            double percentage
    ) {}
}
