package com.capstone.domain.user.dto.response;

public record MyPageResponseDto(
    String email,
    String nickname,
    Long cloverBalance,
    String cloverComment
) {}
