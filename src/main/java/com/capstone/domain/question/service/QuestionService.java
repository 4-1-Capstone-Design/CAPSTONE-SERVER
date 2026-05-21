package com.capstone.domain.question.service;

import com.capstone.domain.journal.dto.response.JournalKeywordItemDto;
import com.capstone.domain.journal.service.EmotionAnalysisService;
import com.capstone.domain.keyword.entity.Keyword;
import com.capstone.domain.keyword.repository.KeywordRepository;
import com.capstone.domain.question.dto.request.QuestionAnswerRequestDto;
import com.capstone.domain.question.dto.response.DailyQuestionItemDto;
import com.capstone.domain.question.dto.response.QuestionAnswerResponseDto;
import com.capstone.domain.question.dto.response.TodayQuestionsResponseDto;
import com.capstone.domain.question.entity.*;
import com.capstone.domain.question.repository.*;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private static final int DAILY_QUESTION_COUNT = 4;
    private static final int RECENT_SHOW_DAYS = 30;
    private static final int HISTORY_LOOKBACK_DAYS = 14;
    private static final List<String> CATEGORIES = List.of("감정", "관계", "일상", "성장", "건강");

    private final QuestionRepository questionRepository;
    private final DailyQuestionRepository dailyQuestionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionAnswerKeywordRepository questionAnswerKeywordRepository;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final EmotionAnalysisService emotionAnalysisService;

    @Transactional
    public TodayQuestionsResponseDto getTodayQuestions(Long userId) {
        LocalDate today = LocalDate.now();

        List<DailyQuestion> existing =
                dailyQuestionRepository.findAllByUserIdAndQuestionDateOrderByDisplayOrder(userId, today);

        if (!existing.isEmpty()) {
            return toResponse(today, existing);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

        List<Question> selected = selectQuestionsForUser(userId, today);

        List<DailyQuestion> created = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            created.add(dailyQuestionRepository.save(
                    DailyQuestion.create(user, selected.get(i), today, i + 1)
            ));
        }

        return toResponse(today, created);
    }

    @Transactional
    public QuestionAnswerResponseDto submitAnswer(Long userId, Long dailyQuestionId,
                                                  QuestionAnswerRequestDto request) {
        DailyQuestion dailyQuestion = dailyQuestionRepository.findByIdAndUserId(dailyQuestionId, userId)
                .orElseThrow(() -> new BusinessException(ErrorStatus.DAILY_QUESTION_NOT_FOUND));

        if (questionAnswerRepository.existsByDailyQuestionId(dailyQuestionId)) {
            throw new BusinessException(ErrorStatus.QUESTION_ALREADY_ANSWERED);
        }

        QuestionAnswer answer = questionAnswerRepository.save(
                QuestionAnswer.create(dailyQuestion, request.content())
        );

        List<String> keywordNames = extractAndSaveKeywords(answer, request.content());

        return new QuestionAnswerResponseDto(
                answer.getId(),
                dailyQuestionId,
                answer.getContent(),
                answer.getCreatedAt(),
                keywordNames
        );
    }

    // ── 질문 선택 로직 ──────────────────────────────────────────────

    private List<Question> selectQuestionsForUser(Long userId, LocalDate today) {
        List<Long> recentIds = dailyQuestionRepository.findRecentQuestionIdsByUserId(
                userId, today.minusDays(RECENT_SHOW_DAYS));

        List<Question> all = questionRepository.findAllByIsActiveTrue();

        List<Question> available = all.stream()
                .filter(q -> !recentIds.contains(q.getId()))
                .collect(Collectors.toList());

        // 가용 질문이 부족하면 전체 풀에서 선택
        if (available.size() < DAILY_QUESTION_COUNT) {
            available = new ArrayList<>(all);
        }

        return selectAdaptively(userId, available);
    }

    private List<Question> selectAdaptively(Long userId, List<Question> available) {
        Map<String, List<Question>> byCategory = available.stream()
                .collect(Collectors.groupingBy(Question::getCategory));

        // 최근 14일 답변 기준 카테고리별 횟수 → 적게 답변한 카테고리 우선
        Map<String, Long> historyCounts = getAnswerCountByCategory(userId);

        List<String> prioritized = CATEGORIES.stream()
                .filter(byCategory::containsKey)
                .sorted(Comparator.comparingLong(c -> historyCounts.getOrDefault(c, 0L)))
                .collect(Collectors.toList());

        List<Question> selected = new ArrayList<>();
        Set<Long> pickedIds = new HashSet<>();
        Random random = new Random();
        int attempts = 0;

        while (selected.size() < DAILY_QUESTION_COUNT
                && attempts < prioritized.size() * DAILY_QUESTION_COUNT) {
            String category = prioritized.get(attempts % prioritized.size());
            List<Question> candidates = byCategory.getOrDefault(category, List.of()).stream()
                    .filter(q -> !pickedIds.contains(q.getId()))
                    .collect(Collectors.toList());

            if (!candidates.isEmpty()) {
                Question picked = candidates.get(random.nextInt(candidates.size()));
                selected.add(picked);
                pickedIds.add(picked.getId());
            }
            attempts++;
        }

        // 남은 슬롯을 랜덤으로 채움
        if (selected.size() < DAILY_QUESTION_COUNT) {
            List<Question> remaining = available.stream()
                    .filter(q -> !pickedIds.contains(q.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(remaining);
            for (Question q : remaining) {
                if (selected.size() >= DAILY_QUESTION_COUNT) break;
                selected.add(q);
            }
        }

        return selected;
    }

    private Map<String, Long> getAnswerCountByCategory(Long userId) {
        LocalDate since = LocalDate.now().minusDays(HISTORY_LOOKBACK_DAYS);
        List<Object[]> rows = dailyQuestionRepository.countAnsweredByCategory(userId, since);
        return rows.stream().collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
        ));
    }

    // ── 키워드 추출 및 저장 (JournalService 패턴 동일) ────────────────

    private List<String> extractAndSaveKeywords(QuestionAnswer savedAnswer, String content) {
        try {
            List<JournalKeywordItemDto> emotions = emotionAnalysisService.analyzeEmotion(content);
            List<String> names = new ArrayList<>();

            for (JournalKeywordItemDto item : emotions) {
                Keyword keyword = keywordRepository.findByName(item.keyword())
                        .orElseGet(() -> keywordRepository.save(
                                Keyword.builder().name(item.keyword()).build()
                        ));

                questionAnswerKeywordRepository.save(
                        QuestionAnswerKeyword.builder()
                                .questionAnswer(savedAnswer)
                                .keyword(keyword)
                                .score(item.score())
                                .build()
                );
                names.add(item.keyword());
            }
            return names;

        } catch (Exception e) {
            log.warn("키워드 추출 실패 (answerId={}): {}", savedAnswer.getId(), e.getMessage());
            return List.of();
        }
    }

    // ── 응답 매핑 ──────────────────────────────────────────────────

    private TodayQuestionsResponseDto toResponse(LocalDate date, List<DailyQuestion> dailyQuestions) {
        List<DailyQuestionItemDto> items = dailyQuestions.stream()
                .map(dq -> {
                    DailyQuestionItemDto.AnswerDto answerDto = null;
                    if (dq.getAnswer() != null) {
                        QuestionAnswer a = dq.getAnswer();
                        List<String> keywords = a.getKeywords().stream()
                                .map(k -> k.getKeyword().getName())
                                .toList();
                        answerDto = new DailyQuestionItemDto.AnswerDto(
                                a.getId(), a.getContent(), a.getCreatedAt(), keywords);
                    }
                    return new DailyQuestionItemDto(
                            dq.getId(),
                            dq.getQuestion().getContent(),
                            dq.getQuestion().getCategory(),
                            dq.getDisplayOrder(),
                            answerDto
                    );
                })
                .toList();

        return new TodayQuestionsResponseDto(date, items);
    }
}
