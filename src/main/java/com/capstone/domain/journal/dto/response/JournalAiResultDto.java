package com.capstone.domain.journal.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record JournalAiResultDto(
    String reply,
    String summary
) {
  public record KeywordItem(
      String name,
      BigDecimal score
  ) {
  }
}