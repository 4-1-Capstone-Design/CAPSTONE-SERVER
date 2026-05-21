package com.capstone.domain.auth.controller;

import com.capstone.domain.auth.dto.request.LoginRequestDto;
import com.capstone.domain.auth.dto.request.LogoutRequestDto;
import com.capstone.domain.auth.dto.request.SignUpRequestDto;
import com.capstone.domain.auth.dto.response.LoginResponseDto;
import com.capstone.domain.auth.dto.response.SignUpResponseDto;
import com.capstone.domain.auth.service.AuthService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "회원가입")
  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(
      @Valid @RequestBody SignUpRequestDto request
  ) {
    SignUpResponseDto response = authService.signUp(request);
    return ResponseEntity.status(SuccessStatus.CREATED.getHttpStatus())
        .body(ApiResponse.created(SuccessStatus.CREATED, response));
  }

  @Operation(summary = "로그인")
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponseDto>> login(
      @Valid @RequestBody LoginRequestDto request
  ) {
    LoginResponseDto response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, response));
  }

  @Operation(summary = "로그아웃")
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @Valid @RequestBody LogoutRequestDto request
  ) {
    authService.logout(request);
    return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK));
  }
}