package com.capstone.domain.journal.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record JournalAnalyzeResponseDto(
    Long journalId,
    String summary,
    String reply,
    List<JournalKeywordItemDto> keywords
) {
}