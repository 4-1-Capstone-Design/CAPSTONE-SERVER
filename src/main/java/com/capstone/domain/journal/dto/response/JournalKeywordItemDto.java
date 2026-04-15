package com.capstone.domain.journal.dto.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record JournalKeywordItemDto(
    String keyword,
    BigDecimal score
) {
}
