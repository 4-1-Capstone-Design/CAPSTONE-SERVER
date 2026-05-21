package com.capstone.domain.journal.controller;

import com.capstone.domain.journal.dto.request.JournalCreateRequestDto;
import com.capstone.domain.journal.dto.response.JournalAnalyzeResponseDto;
import com.capstone.domain.journal.dto.response.JournalCreateResponseDto;
import com.capstone.domain.journal.dto.response.JournalCursorResponseDto;
import com.capstone.domain.journal.dto.response.JournalGetResponseDto;
import com.capstone.domain.journal.dto.response.JournalKeywordItemDto;
import com.capstone.domain.journal.service.JournalService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import com.capstone.global.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/journals")
@RequiredArgsConstructor
public class JournalController {

  private final JournalService journalService;

  @PostMapping
  public ResponseEntity<ApiResponse<JournalCreateResponseDto>> createJournal(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @Valid @RequestBody JournalCreateRequestDto request
  ) {
    JournalCreateResponseDto response = journalService.createJournal(userPrincipal.getUserId(), request);

    return ResponseEntity.status(SuccessStatus.CREATED.getHttpStatus())
        .body(ApiResponse.created(SuccessStatus.CREATED, response));
  }

  @DeleteMapping("/{journalId}")
  public ResponseEntity<ApiResponse<Void>> deleteJournal(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @PathVariable Long journalId
  ) {
    journalService.deleteJournal(userPrincipal.getUserId(), journalId);

    return ResponseEntity.status(SuccessStatus.OK.getHttpStatus())
        .body(ApiResponse.success(SuccessStatus.OK));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<JournalCursorResponseDto>> getJournalList(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "10") int size
  ) {
    JournalCursorResponseDto response = journalService.getJournalList(userPrincipal.getUserId(), cursor, size);

    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }

  @GetMapping("/by-date")
  public ResponseEntity<ApiResponse<JournalGetResponseDto>> getJournalByDate(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @RequestParam int year,
      @RequestParam int month,
      @RequestParam int day
  ) {
    LocalDate date = LocalDate.of(year, month, day);

    JournalGetResponseDto response = journalService.getJournalByDate(userPrincipal.getUserId(), date);

    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }

  @PostMapping("/{journalId}/reply")
  public ResponseEntity<ApiResponse<JournalAnalyzeResponseDto>> createJournalReply(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @PathVariable Long journalId
  ) {
    JournalAnalyzeResponseDto response =
        journalService.createJournalReply(userPrincipal.getUserId(), journalId);

    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }

  @GetMapping("/{journalId}/keywords")
  public ResponseEntity<ApiResponse<List<JournalKeywordItemDto>>> getKeywords(
      @AuthenticationPrincipal UserPrincipal userPrincipal,
      @PathVariable Long journalId
  ) {
    List<JournalKeywordItemDto> response =
        journalService.getKeywords(userPrincipal.getUserId(), journalId);

    return ResponseEntity.ok(
        ApiResponse.success(SuccessStatus.OK, response)
    );
  }
}