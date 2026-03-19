package com.capstone.domain.auth.entity;

import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
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
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new BusinessException(ErrorStatus.INVALID_REFRESH_TOKEN);
    }
    if (ttl == null || ttl <= 0) {
      throw new BusinessException(ErrorStatus.BAD_REQUEST);
    }
    this.refreshToken = refreshToken;
    this.ttl = ttl;
  }
}