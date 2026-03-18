package com.capstone.domain.user.controller;

import com.capstone.domain.user.dto.request.SignUpRequestDto;
import com.capstone.domain.user.dto.response.SignUpResponseDto;
import com.capstone.domain.user.service.UserService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(
      @Valid @RequestBody SignUpRequestDto request
  ) {
    SignUpResponseDto response = userService.signUp(request);
    return ResponseEntity.status(SuccessStatus.CREATED.getHttpStatus())
        .body(ApiResponse.created(SuccessStatus.CREATED, response));
  }
}