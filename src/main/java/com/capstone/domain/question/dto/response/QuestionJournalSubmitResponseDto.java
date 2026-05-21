package com.capstone.domain.question.dto.response;

import java.time.LocalDate;

public record QuestionJournalSubmitResponseDto(
        Long journalId,
        String title,
        LocalDate journalDate
) {}
