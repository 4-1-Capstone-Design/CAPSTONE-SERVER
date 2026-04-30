package com.capstone.domain.clover.controller;

import com.capstone.domain.clover.dto.response.CloverBalanceResponseDto;
import com.capstone.domain.clover.service.CloverService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import com.capstone.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clovers")
@RequiredArgsConstructor
public class CloverController {

  private final CloverService cloverService;
  private final JwtTokenProvider jwtTokenProvider;

  @GetMapping
  public ResponseEntity<ApiResponse<CloverBalanceResponseDto>> getCloverBalance(
      @RequestHeader("Authorization") String bearerToken
  ) {
    String token = bearerToken.replace("Bearer ", "");
    Long userId = jwtTokenProvider.getUserIdFromToken(token);

    CloverBalanceResponseDto response = cloverService.getCloverBalance(userId);

    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }
}