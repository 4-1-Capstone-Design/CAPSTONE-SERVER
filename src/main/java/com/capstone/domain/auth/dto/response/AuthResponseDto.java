package com.capstone.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthResponseDto(  String accessToken,
                                String refreshToken,
                                boolean isNewUser,
                                Long userId
) {
}