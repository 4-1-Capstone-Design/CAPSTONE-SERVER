package com.capstone.domain.journal.entity;

import com.capstone.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journal")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Journal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 255)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "journal_date", nullable = false)
  private LocalDate journalDate;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "journal")
  private List<JournalReply> journalReplies = new ArrayList<>();

  @OneToMany(mappedBy = "journal")
  private List<JournalAnalysis> journalAnalyses = new ArrayList<>();

  @Builder
  public Journal(String title, String content, LocalDate journalDate,
      LocalDateTime createdAt, Boolean isDeleted, User user) {
    this.title = title;
    this.content = content;
    this.journalDate = journalDate;
    this.createdAt = createdAt;
    this.isDeleted = isDeleted;
    this.user = user;
  }

  public static Journal create(String title, String content, LocalDate journalDate, User user) {
    return Journal.builder()
        .title(title)
        .content(content)
        .journalDate(journalDate)
        .createdAt(LocalDateTime.now())
        .isDeleted(false)
        .user(user)
        .build();
  }

  public void delete() {
    this.isDeleted = true;
  }
}