package com.capstone.domain.user.entity;

import com.capstone.domain.clover.entity.CloverHistory;
import com.capstone.domain.journal.entity.Journal;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255, unique = true)
  private String email;

  @Column(nullable = false, length = 255)
  private String password;

  @Column(nullable = false, length = 20, unique = true)
  private String nickname;

  @Column(name = "clover_balance", nullable = false)
  private Long cloverBalance;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "user")
  private List<Journal> journals = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  private List<CloverHistory> cloverHistories = new ArrayList<>();

  @Builder
  public User(String email, String password, String nickname, Long cloverBalance,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.cloverBalance = cloverBalance;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}