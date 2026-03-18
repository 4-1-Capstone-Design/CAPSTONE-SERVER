package com.capstone.domain.user.dto.response;

import lombok.Builder;

@Builder
public record SignUpResponseDto(
    Long userId,
    String email,
    String nickname
) {
}