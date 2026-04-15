package com.capstone.domain.journal.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record JournalCursorResponseDto(
    List<JournalListItemResponseDto> journals,
    Long nextCursor,
    boolean hasNext
) {
}