package com.capstone.domain.journal.entity;

import com.capstone.domain.keyword.entity.Keyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "journal_keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JournalKeyword {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, precision = 5, scale = 4)
  private BigDecimal score;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "analysis_id", nullable = false)
  private JournalAnalysis journalAnalysis;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "keyword_id", nullable = false)
  private Keyword keyword;

  @Builder
  public JournalKeyword(BigDecimal score, JournalAnalysis journalAnalysis, Keyword keyword) {
    this.score = score;
    this.journalAnalysis = journalAnalysis;
    this.keyword = keyword;
  }
}