package com.capstone.domain.journal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

  @Entity
  @Table(name = "journal_reply")
  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public class JournalReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", nullable = false)
    private Journal journal;

    @Builder
    public JournalReply(String content, String modelName, LocalDateTime createdAt, Journal journal) {
      this.content = content;
      this.modelName = modelName;
      this.createdAt = createdAt;
      this.journal = journal;
    }
  }