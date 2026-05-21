package com.capstone.domain.clover.controller;

import com.capstone.domain.clover.dto.response.CloverBalanceResponseDto;
import com.capstone.domain.clover.service.CloverService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import com.capstone.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clovers")
@RequiredArgsConstructor
public class CloverController {

  private final CloverService cloverService;

  @GetMapping
  public ResponseEntity<ApiResponse<CloverBalanceResponseDto>> getCloverBalance(
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    CloverBalanceResponseDto response = cloverService.getCloverBalance(userPrincipal.getUserId());

    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }
}