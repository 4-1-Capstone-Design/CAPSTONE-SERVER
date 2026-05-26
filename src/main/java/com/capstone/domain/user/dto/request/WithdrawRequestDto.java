package com.capstone.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WithdrawRequestDto(
    @NotBlank String password
) {}
