package com.capstone.domain.question.dto.response;

import java.time.LocalDate;
import java.util.List;

public record TodayQuestionsResponseDto(
        LocalDate questionDate,
        List<DailyQuestionItemDto> questions
) {}
