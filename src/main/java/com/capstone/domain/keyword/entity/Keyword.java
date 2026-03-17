package com.capstone.domain.keyword.entity;

import com.capstone.domain.journal.entity.JournalKeyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "keyword_name", nullable = false, length = 255)
  private String keywordName;

  @OneToMany(mappedBy = "keyword")
  private List<JournalKeyword> journalKeywords = new ArrayList<>();

  @Builder
  public Keyword(String keywordName) {
    this.keywordName = keywordName;
  }
}