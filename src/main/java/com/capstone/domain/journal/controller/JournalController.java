package com.capstone.domain.journal.controller;

import com.capstone.domain.journal.dto.request.JournalCreateRequestDto;
import com.capstone.domain.journal.dto.response.JournalCreateResponseDto;
import com.capstone.domain.journal.dto.response.JournalCursorResponseDto;
import com.capstone.domain.journal.dto.response.JournalGetResponseDto;
import com.capstone.domain.journal.dto.response.JournalReplyResponseDto;
import com.capstone.domain.journal.service.JournalService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import com.capstone.global.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

  @DeleteMapping("/{journalId}")
  public ResponseEntity<ApiResponse<Void>> deleteJournal(
      @RequestHeader("Authorization") String bearerToken,
      @PathVariable Long journalId
  ) {
    String token = bearerToken.replace("Bearer ", "");
    Long userId = jwtTokenProvider.getUserIdFromToken(token);

    journalService.deleteJournal(userId, journalId);

    return ResponseEntity.status(SuccessStatus.OK.getHttpStatus())
        .body(ApiResponse.success(SuccessStatus.OK));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<JournalCursorResponseDto>> getJournalList(
      @RequestHeader("Authorization") String bearerToken,
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "10") int size
  ) {
    String token = bearerToken.replace("Bearer ", "");
    Long userId = jwtTokenProvider.getUserIdFromToken(token);

    JournalCursorResponseDto response = journalService.getJournalList(userId, cursor, size);

    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }

  @GetMapping("/by-date")
  public ResponseEntity<ApiResponse<JournalGetResponseDto>> getJournalByDate(
      @RequestHeader("Authorization") String bearerToken,
      @RequestParam int year,
      @RequestParam int month,
      @RequestParam int day
  ) {
    String token = bearerToken.replace("Bearer ", "");
    Long userId = jwtTokenProvider.getUserIdFromToken(token);

    LocalDate date = LocalDate.of(year, month, day);

    JournalGetResponseDto response = journalService.getJournalByDate(userId, date);

    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }

  @PostMapping("/{journalId}/reply")
  public ResponseEntity<ApiResponse<JournalReplyResponseDto>> createJournalReply(
      @RequestHeader("Authorization") String bearerToken,
      @PathVariable Long journalId
  ) {
    String token = bearerToken.replace("Bearer ", "");
    Long userId = jwtTokenProvider.getUserIdFromToken(token);

    JournalReplyResponseDto response = journalService.createJournalReply(userId, journalId);

    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }
}