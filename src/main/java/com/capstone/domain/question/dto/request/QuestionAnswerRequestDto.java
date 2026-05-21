package com.capstone.domain.question.dto.request;

import jakarta.validation.constraints.NotBlank;

public record QuestionAnswerRequestDto(
        @NotBlank(message = "답변 내용은 비어있을 수 없습니다.") String content
) {}
