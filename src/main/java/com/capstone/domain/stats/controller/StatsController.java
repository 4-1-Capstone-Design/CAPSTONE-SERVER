package com.capstone.domain.stats.controller;

import com.capstone.domain.stats.dto.MonthlyStatsResponseDto;
import com.capstone.domain.stats.service.StatsService;
import com.capstone.global.common.ApiResponse;
import com.capstone.global.common.SuccessStatus;
import com.capstone.global.security.UserPrincipal;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

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
