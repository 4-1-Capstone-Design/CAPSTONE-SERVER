package com.capstone.domain.question.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionAnswerResponseDto(
        Long answerId,
        Long dailyQuestionId,
        String content,
        LocalDateTime createdAt,
        List<String> keywords
) {}
