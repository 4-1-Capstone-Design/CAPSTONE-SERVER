package com.capstone.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record LoginResponseDto(
    Long userId,
    String email,
    String nickname,
    String accessToken,
    String refreshToken
) {
}