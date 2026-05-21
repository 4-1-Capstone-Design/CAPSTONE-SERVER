package com.capstone.domain.question.controller;

import com.capstone.domain.question.dto.request.QuestionAnswerRequestDto;
import com.capstone.domain.question.dto.response.QuestionAnswerResponseDto;
import com.capstone.domain.question.dto.response.TodayQuestionsResponseDto;
import com.capstone.domain.question.service.QuestionService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import com.capstone.global.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<TodayQuestionsResponseDto>> getTodayQuestions(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TodayQuestionsResponseDto response = questionService.getTodayQuestions(userPrincipal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, response));
    }

    @PostMapping("/{dailyQuestionId}/answers")
    public ResponseEntity<ApiResponse<QuestionAnswerResponseDto>> submitAnswer(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long dailyQuestionId,
            @Valid @RequestBody QuestionAnswerRequestDto request
    ) {
        QuestionAnswerResponseDto response = questionService.submitAnswer(userPrincipal.getUserId(), dailyQuestionId, request);
        return ResponseEntity.status(SuccessStatus.CREATED.getHttpStatus())
                .body(ApiResponse.created(SuccessStatus.CREATED, response));
    }
}
