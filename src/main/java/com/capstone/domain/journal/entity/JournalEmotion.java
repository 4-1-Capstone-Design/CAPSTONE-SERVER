package com.capstone.domain.journal.entity;

import com.capstone.domain.emotion.entity.EmotionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "journal_emotion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JournalEmotion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, precision = 5, scale = 4)
  private BigDecimal score;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "journal_analysis_id", nullable = false)
  private JournalAnalysis journalAnalysis;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emotion_type_id", nullable = false)
  private EmotionType emotionType;

  @Builder
  public JournalEmotion(BigDecimal score, JournalAnalysis journalAnalysis, EmotionType emotionType) {
    this.score = score;
    this.journalAnalysis = journalAnalysis;
    this.emotionType = emotionType;
  }
}