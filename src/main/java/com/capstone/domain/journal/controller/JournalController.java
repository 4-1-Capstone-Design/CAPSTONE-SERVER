package com.capstone.domain.journal.controller;

import com.capstone.domain.journal.dto.request.JournalCreateRequestDto;
import com.capstone.domain.journal.dto.response.JournalCreateResponseDto;
import com.capstone.domain.journal.service.JournalService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import com.capstone.global.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/journals")
@RequiredArgsConstructor
public class JournalController {

  private final JournalService journalService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping
  public ResponseEntity<ApiResponse<JournalCreateResponseDto>> createJournal(
      @RequestHeader("Authorization") String bearerToken,
      @Valid @RequestBody JournalCreateRequestDto request
  ) {
    String token = bearerToken.replace("Bearer ", "");
    Long userId = jwtTokenProvider.getUserIdFromToken(token);

    JournalCreateResponseDto response = journalService.createJournal(userId, request);

    return ResponseEntity.status(SuccessStatus.CREATED.getHttpStatus())
        .body(ApiResponse.created(SuccessStatus.CREATED, response));
  }
}