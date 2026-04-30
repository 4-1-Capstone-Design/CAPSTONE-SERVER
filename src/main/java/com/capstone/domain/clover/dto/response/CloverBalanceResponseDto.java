package com.capstone.domain.clover.dto.response;

import lombok.Builder;

@Builder
public record CloverBalanceResponseDto(
    Long cloverBalance
) {
}