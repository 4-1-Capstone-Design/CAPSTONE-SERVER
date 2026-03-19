package com.capstone.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record SignUpResponseDto(
    Long userId,
    String email,
    String nickname
) {
}