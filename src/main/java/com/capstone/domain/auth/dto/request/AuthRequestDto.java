package com.capstone.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDto (
  @NotBlank(message = "토큰은 필수입니다.")
  String token
) {
  }