package com.capstone.domain.question.service;

import com.capstone.domain.journal.dto.request.JournalCreateRequestDto;
import com.capstone.domain.journal.dto.response.JournalCreateResponseDto;
import com.capstone.domain.journal.dto.response.JournalKeywordItemDto;
import com.capstone.domain.journal.repository.JournalKeywordRepository;
import com.capstone.domain.journal.service.EmotionAnalysisService;
import com.capstone.domain.journal.service.JournalService;
import com.capstone.domain.keyword.entity.Keyword;
import com.capstone.domain.keyword.repository.KeywordRepository;
import com.capstone.domain.question.dto.request.QuestionJournalSubmitRequestDto;
import com.capstone.domain.question.dto.response.DailyQuestionItemDto;
import com.capstone.domain.question.dto.response.QuestionJournalSubmitResponseDto;
import com.capstone.domain.question.dto.response.TodayQuestionsResponseDto;
import com.capstone.domain.question.entity.*;
import com.capstone.domain.question.repository.*;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private static final int EMOTION_LOOKBACK_DAYS = 7;
    private static final List<String> CATEGORIES = List.of("감정", "관계", "일상", "성장", "건강");

    // BERT 모델(hun3359/klue-bert-base-sentiment) 60개 감정 레이블 → 질문 카테고리 가중치 매핑
    // 해당 감정이 높을 때 탐색이 도움이 되는 카테고리를 우선순위에 반영
    private static final Map<String, List<String>> EMOTION_CATEGORY_BOOST;
    static {
        Map<String, List<String>> m = new HashMap<>();
        // 분노 계열 (0-9)
        m.put("분노",        List.of("감정", "관계"));
        m.put("툴툴대는",    List.of("감정", "관계"));
        m.put("좌절한",      List.of("성장", "감정"));
        m.put("짜증내는",    List.of("관계", "감정"));
        m.put("방어적인",    List.of("관계", "감정"));
        m.put("악의적인",    List.of("관계", "감정"));
        m.put("안달하는",    List.of("건강", "감정"));
        m.put("구역질 나는", List.of("건강", "감정"));
        m.put("노여워하는",  List.of("감정", "관계"));
        m.put("성가신",      List.of("감정", "관계"));
        // 슬픔 계열 (10-19)
        m.put("슬픔",           List.of("감정", "관계"));
        m.put("실망한",         List.of("감정", "성장"));
        m.put("비통한",         List.of("감정", "관계"));
        m.put("후회되는",       List.of("성장", "감정"));
        m.put("우울한",         List.of("건강", "감정"));
        m.put("마비된",         List.of("건강", "감정"));
        m.put("염세적인",       List.of("감정", "성장"));
        m.put("눈물이 나는",    List.of("감정", "관계"));
        m.put("낙담한",         List.of("감정", "성장"));
        m.put("환멸을 느끼는",  List.of("감정", "성장"));
        // 불안 계열 (20-29)
        m.put("불안",        List.of("감정", "건강"));
        m.put("두려운",      List.of("감정", "건강"));
        m.put("스트레스 받는", List.of("건강"));
        m.put("취약한",      List.of("감정", "건강"));
        m.put("혼란스러운",  List.of("감정"));
        m.put("당혹스러운",  List.of("감정"));
        m.put("회의적인",    List.of("성장", "감정"));
        m.put("걱정스러운",  List.of("건강", "감정"));
        m.put("조심스러운",  List.of("건강", "감정"));
        m.put("초조한",      List.of("건강", "감정"));
        // 상처 계열 (30-39)
        m.put("상처",          List.of("관계", "감정"));
        m.put("질투하는",      List.of("관계", "감정"));
        m.put("배신당한",      List.of("관계", "감정"));
        m.put("고립된",        List.of("관계", "감정"));
        m.put("충격 받은",     List.of("감정"));
        m.put("가난한 불우한", List.of("감정", "일상"));
        m.put("희생된",        List.of("감정", "관계"));
        m.put("억울한",        List.of("관계", "감정"));
        m.put("괴로워하는",    List.of("감정", "건강"));
        m.put("버려진",        List.of("관계", "감정"));
        // 당황 계열 (40-49)
        m.put("당황",              List.of("감정"));
        m.put("고립된(당황한)",    List.of("관계", "감정"));
        m.put("남의 시선을 의식하는", List.of("관계", "감정"));
        m.put("외로운",            List.of("관계", "감정"));
        m.put("열등감",            List.of("성장", "감정"));
        m.put("죄책감의",          List.of("감정", "성장"));
        m.put("부끄러운",          List.of("감정"));
        m.put("혐오스러운",        List.of("감정"));
        m.put("한심한",            List.of("감정", "성장"));
        m.put("혼란스러운(당황한)", List.of("감정"));
        // 기쁨 계열 (50-59)
        m.put("기쁨",      List.of("일상", "성장"));
        m.put("감사하는",  List.of("관계", "일상"));
        m.put("신뢰하는",  List.of("관계", "성장"));
        m.put("편안한",    List.of("일상", "건강"));
        m.put("만족스러운", List.of("성장", "일상"));
        m.put("흥분",      List.of("일상", "성장"));
        m.put("느긋",      List.of("일상", "건강"));
        m.put("안도",      List.of("건강", "일상"));
        m.put("신이 난",   List.of("일상", "성장"));
        m.put("자신하는",  List.of("성장"));
        EMOTION_CATEGORY_BOOST = Collections.unmodifiableMap(m);
    }

    private final QuestionRepository questionRepository;
    private final DailyQuestionRepository dailyQuestionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionAnswerKeywordRepository questionAnswerKeywordRepository;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final EmotionAnalysisService emotionAnalysisService;
    private final JournalService journalService;
    private final JournalKeywordRepository journalKeywordRepository;

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

        if (selected.size() < DAILY_QUESTION_COUNT) {
            throw new BusinessException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            List<DailyQuestion> created = new ArrayList<>();
            for (int i = 0; i < selected.size(); i++) {
                created.add(dailyQuestionRepository.save(
                        DailyQuestion.create(user, selected.get(i), today, i + 1)
                ));
            }
            return toResponse(today, created);
        } catch (DataIntegrityViolationException e) {
            List<DailyQuestion> reloaded =
                    dailyQuestionRepository.findAllByUserIdAndQuestionDateOrderByDisplayOrder(userId, today);
            if (!reloaded.isEmpty()) {
                return toResponse(today, reloaded);
            }
            throw e;
        }
    }

    @Transactional
    public QuestionJournalSubmitResponseDto submitAllAnswers(Long userId,
                                                             QuestionJournalSubmitRequestDto request) {
        LocalDate today = LocalDate.now();

        for (QuestionJournalSubmitRequestDto.AnswerItemDto item : request.answers()) {
            DailyQuestion dailyQuestion = dailyQuestionRepository
                    .findByIdAndUserId(item.dailyQuestionId(), userId)
                    .orElseThrow(() -> new BusinessException(ErrorStatus.DAILY_QUESTION_NOT_FOUND));

            if (questionAnswerRepository.existsByDailyQuestionId(item.dailyQuestionId())) {
                throw new BusinessException(ErrorStatus.QUESTION_ALREADY_ANSWERED);
            }

            QuestionAnswer answer;
            try {
                answer = questionAnswerRepository.save(
                        QuestionAnswer.create(dailyQuestion, item.content())
                );
            } catch (DataIntegrityViolationException e) {
                throw new BusinessException(ErrorStatus.QUESTION_ALREADY_ANSWERED);
            }

            extractAndSaveKeywords(answer, item.content());
        }

        String journalContent = buildJournalContent(userId, today, request.answers());

        JournalCreateResponseDto journalResponse = journalService.createJournal(
                userId,
                new JournalCreateRequestDto(request.title(), journalContent, today)
        );

        return new QuestionJournalSubmitResponseDto(
                journalResponse.journalId(),
                journalResponse.title(),
                journalResponse.journalDate()
        );
    }

    private String buildJournalContent(Long userId, LocalDate date,
                                       List<QuestionJournalSubmitRequestDto.AnswerItemDto> answers) {
        List<DailyQuestion> dailyQuestions = dailyQuestionRepository
                .findAllByUserIdAndQuestionDateOrderByDisplayOrder(userId, date);

        Map<Long, String> questionTextById = dailyQuestions.stream()
                .collect(Collectors.toMap(DailyQuestion::getId, dq -> dq.getQuestion().getContent()));

        StringBuilder sb = new StringBuilder();
        for (QuestionJournalSubmitRequestDto.AnswerItemDto item : answers) {
            String questionText = questionTextById.getOrDefault(item.dailyQuestionId(), "");
            if (!sb.isEmpty()) sb.append("\n\n");
            sb.append("Q. ").append(questionText).append("\n");
            sb.append(item.content());
        }
        return sb.toString();
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

        // 최근 14일 답변 기준 카테고리별 횟수 + 최근 7일 감정 기반 가중치 합산
        Map<String, Long> historyCounts = getAnswerCountByCategory(userId);
        Map<String, Double> emotionBoost = getEmotionCategoryBoost(userId);

        // 유효 점수 = 답변 횟수 - 감정 가중치 (낮을수록 우선순위 높음)
        List<String> prioritized = CATEGORIES.stream()
                .filter(byCategory::containsKey)
                .sorted(Comparator.comparingDouble(c ->
                        historyCounts.getOrDefault(c, 0L) - emotionBoost.getOrDefault(c, 0.0)))
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

    private Map<String, Double> getEmotionCategoryBoost(Long userId) {
        LocalDate since = LocalDate.now().minusDays(EMOTION_LOOKBACK_DAYS);
        List<Object[]> rows = journalKeywordRepository.findRecentEmotionScores(userId, since);

        Map<String, Double> boost = new HashMap<>();
        for (String cat : CATEGORIES) boost.put(cat, 0.0);

        for (Object[] row : rows) {
            String emotionName = (String) row[0];
            double score = ((BigDecimal) row[1]).doubleValue();
            List<String> targets = EMOTION_CATEGORY_BOOST.getOrDefault(emotionName, List.of());
            for (String cat : targets) {
                boost.merge(cat, score, Double::sum);
            }
        }
        return boost;
    }

    // ── 키워드 추출 및 저장 (JournalService 패턴 동일) ────────────────

    private List<String> extractAndSaveKeywords(QuestionAnswer savedAnswer, String content) {
        List<JournalKeywordItemDto> emotions;
        try {
            emotions = emotionAnalysisService.analyzeEmotion(content);
        } catch (Exception e) {
            log.warn("키워드 추출 실패 (answerId={}): {}", savedAnswer.getId(), e.getMessage());
            return List.of();
        }

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
