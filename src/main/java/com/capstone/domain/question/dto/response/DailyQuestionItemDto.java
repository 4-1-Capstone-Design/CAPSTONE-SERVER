package com.capstone.domain.question.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record DailyQuestionItemDto(
        Long dailyQuestionId,
        String content,
        String category,
        Integer displayOrder,
        AnswerDto answer
) {
    public record AnswerDto(
            Long answerId,
            String content,
            LocalDateTime createdAt,
            List<String> keywords
    ) {}
}
