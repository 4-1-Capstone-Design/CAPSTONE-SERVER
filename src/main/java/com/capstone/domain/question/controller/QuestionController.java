package com.capstone.domain.question.controller;

import com.capstone.domain.question.dto.request.QuestionJournalSubmitRequestDto;
import com.capstone.domain.question.dto.response.QuestionJournalSubmitResponseDto;
import com.capstone.domain.question.dto.response.TodayQuestionsResponseDto;
import com.capstone.domain.question.service.QuestionService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import com.capstone.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Question", description = "오늘의 질문 관련 API")
@RestController
@RequestMapping("/api/v1/journals/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "오늘의 질문 목록 조회")
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<TodayQuestionsResponseDto>> getTodayQuestions(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TodayQuestionsResponseDto response = questionService.getTodayQuestions(userPrincipal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, response));
    }

    @Operation(summary = "오늘의 질문 전체 답변 제출 및 저널 생성")
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<QuestionJournalSubmitResponseDto>> submitAllAnswers(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody QuestionJournalSubmitRequestDto request
    ) {
        QuestionJournalSubmitResponseDto response =
                questionService.submitAllAnswers(userPrincipal.getUserId(), request);
        return ResponseEntity.status(SuccessStatus.CREATED.getHttpStatus())
                .body(ApiResponse.created(SuccessStatus.CREATED, response));
    }
}
