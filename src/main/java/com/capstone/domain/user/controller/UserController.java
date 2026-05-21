package com.capstone.domain.user.controller;

import com.capstone.domain.user.dto.response.MyPageResponseDto;
import com.capstone.domain.user.service.UserService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import com.capstone.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @Operation(summary = "마이페이지 조회")
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<MyPageResponseDto>> getMyPage(
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    MyPageResponseDto response = userService.getMyPage(userPrincipal.getUserId());
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, response));
  }
}