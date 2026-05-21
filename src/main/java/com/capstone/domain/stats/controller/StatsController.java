package com.capstone.domain.stats.controller;

import com.capstone.domain.stats.dto.MonthlyStatsResponseDto;
import com.capstone.domain.stats.service.StatsService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import com.capstone.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Stats", description = "통계 관련 API")
@Validated
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "월별 감정 키워드 통계 조회")
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<MonthlyStatsResponseDto>> getMonthlyStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) @Min(1900) Integer year,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month
    ) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        int targetMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        MonthlyStatsResponseDto response = statsService.getMonthlyStats(userPrincipal.getUserId(), targetYear, targetMonth);

        return ResponseEntity.ok(ApiResponse.success(SuccessStatus.OK, response));
    }
}
