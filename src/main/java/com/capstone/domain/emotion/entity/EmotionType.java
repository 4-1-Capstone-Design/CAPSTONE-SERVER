package com.capstone.domain.emotion.entity;

import com.capstone.domain.journal.entity.JournalEmotion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emotion_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "emotion_name", nullable = false, length = 255)
  private String emotionName;

  @OneToMany(mappedBy = "emotionType")
  private List<JournalEmotion> journalEmotions = new ArrayList<>();

  @Builder
  public EmotionType(String emotionName) {
    this.emotionName = emotionName;
  }
}
