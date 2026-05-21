package com.capstone.domain.stats.service;

import com.capstone.domain.journal.repository.JournalKeywordRepository;
import com.capstone.domain.journal.repository.JournalRepository;
import com.capstone.domain.question.repository.DailyQuestionRepository;
import com.capstone.domain.question.repository.QuestionAnswerKeywordRepository;
import com.capstone.domain.stats.dto.MonthlyStatsResponseDto;
import com.capstone.domain.stats.dto.MonthlyStatsResponseDto.EmotionStatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final JournalRepository journalRepository;
    private final JournalKeywordRepository journalKeywordRepository;
    private final DailyQuestionRepository dailyQuestionRepository;
    private final QuestionAnswerKeywordRepository questionAnswerKeywordRepository;

    public MonthlyStatsResponseDto getMonthlyStats(Long userId, int year, int month) {
        long journalCount = journalRepository.countByUserIdAndMonth(userId, year, month);
        long answerCount = dailyQuestionRepository.countAnsweredByUserAndMonth(userId, year, month);

        List<EmotionStatDto> emotionDistribution = buildEmotionDistribution(userId, year, month);
        String topEmotion = emotionDistribution.isEmpty() ? null : emotionDistribution.get(0).emotion();

        return new MonthlyStatsResponseDto(year, month, journalCount, answerCount, topEmotion, emotionDistribution);
    }

    private List<EmotionStatDto> buildEmotionDistribution(Long userId, int year, int month) {
        // 저널 키워드와 질문 답변 키워드를 합산
        Map<String, Long> countMap = new LinkedHashMap<>();

        List<Object[]> journalStats = journalKeywordRepository.findMonthlyKeywordStats(userId, year, month);
        for (Object[] row : journalStats) {
            String name = (String) row[0];
            long count = (Long) row[1];
            countMap.merge(name, count, Long::sum);
        }

        List<Object[]> answerStats = questionAnswerKeywordRepository.findMonthlyKeywordStats(userId, year, month);
        for (Object[] row : answerStats) {
            String name = (String) row[0];
            long count = (Long) row[1];
            countMap.merge(name, count, Long::sum);
        }

        if (countMap.isEmpty()) {
            return List.of();
        }

        long total = countMap.values().stream().mapToLong(Long::longValue).sum();

        return countMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new EmotionStatDto(
                        e.getKey(),
                        e.getValue(),
                        Math.round((e.getValue() * 1000.0 / total)) / 10.0  // 소수점 1자리 %
                ))
                .collect(Collectors.toList());
    }
}
