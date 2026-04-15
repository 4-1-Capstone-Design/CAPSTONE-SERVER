package com.capstone.domain.journal.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record JournalReplyResponseDto(
    Long replyId,
    Long journalId,
    String content,
    String modelName,
    LocalDateTime createdAt
) {
}