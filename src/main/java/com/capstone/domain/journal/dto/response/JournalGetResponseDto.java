package com.capstone.domain.journal.dto.response;

import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record JournalGetResponseDto(
    Long journalId,
    String title,
    String content,
    LocalDate journalDate,
    LocalDateTime createdAt,
    List<QAItemDto> questions
) {
    public record QAItemDto(
        int displayOrder,
        String question,
        String answer
    ) {}
}