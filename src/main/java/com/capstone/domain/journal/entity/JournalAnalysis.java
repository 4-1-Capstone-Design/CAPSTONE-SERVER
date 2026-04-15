package com.capstone.domain.journal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journal_analysis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JournalAnalysis {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 500)
  private String summary;

  @Column(name = "model_name", length = 100, nullable = false)
  private String modelName;

  @Column(length = 50)
  private String version;

  @Column(name = "analyzed_at", nullable = false)
  private LocalDateTime analyzedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "journal_id", nullable = false)
  private Journal journal;

  @OneToMany(mappedBy = "journalAnalysis")
  private List<JournalKeyword> journalKeywords = new ArrayList<>();

  @Builder
  public JournalAnalysis(String summary, String modelName, String version,
      LocalDateTime analyzedAt, Journal journal) {
    this.summary = summary;
    this.modelName = modelName;
    this.version = version;
    this.analyzedAt = analyzedAt;
    this.journal = journal;
  }
  public static JournalAnalysis create(String summary, String modelName, String version, Journal journal) {
    return JournalAnalysis.builder()
        .summary(summary)
        .modelName(modelName)
        .version(version)
        .analyzedAt(LocalDateTime.now())
        .journal(journal)
        .build();
  }
}