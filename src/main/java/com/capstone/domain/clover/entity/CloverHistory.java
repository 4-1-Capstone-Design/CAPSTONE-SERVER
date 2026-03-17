package com.capstone.domain.clover.entity;

import com.capstone.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "clover_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CloverHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer amount;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Builder
  public CloverHistory(Integer amount, LocalDateTime createdAt, User user) {
    this.amount = amount;
    this.createdAt = createdAt;
    this.user = user;
  }
}