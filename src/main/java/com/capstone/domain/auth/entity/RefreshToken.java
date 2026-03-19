package com.capstone.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "refreshToken")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

  @Id
  private Long userId; // Redis Key: refreshToken:{userId}

  private String refreshToken;

  @TimeToLive
  private Long ttl; // 초 단위

  public void updateRefreshToken(String refreshToken, Long ttl) {
    this.refreshToken = refreshToken;
    this.ttl = ttl;
  }
}