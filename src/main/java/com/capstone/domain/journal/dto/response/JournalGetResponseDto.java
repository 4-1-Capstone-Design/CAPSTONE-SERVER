package com.capstone.domain.journal.dto.response;

import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record JournalGetResponseDto(
    Long journalId,
    String title,
    String content,
    LocalDate journalDate,
    LocalDateTime createdAt
) {}