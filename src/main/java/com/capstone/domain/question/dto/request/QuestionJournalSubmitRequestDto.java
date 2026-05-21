package com.capstone.domain.question.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record QuestionJournalSubmitRequestDto(
        String title,

        @NotEmpty(message = "답변 목록은 비어있을 수 없습니다.")
        @Valid
        List<AnswerItemDto> answers
) {
    public record AnswerItemDto(
            Long dailyQuestionId,

            @NotBlank(message = "답변 내용은 비어있을 수 없습니다.")
            String content
    ) {}
}
