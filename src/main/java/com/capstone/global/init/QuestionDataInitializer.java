package com.capstone.global.init;

import com.capstone.domain.question.entity.Question;
import com.capstone.domain.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionDataInitializer implements ApplicationRunner {

    private final QuestionRepository questionRepository;

    private static final List<Object[]> INITIAL_QUESTIONS = List.of(
            // {content, category}
            new Object[]{"오늘 아침 눈을 떴을 때 첫 번째로 든 감정은 무엇인가요?", "감정"},
            new Object[]{"최근 가장 행복했던 순간을 떠올려보세요. 무엇이 그 감정을 만들었나요?", "감정"},
            new Object[]{"요즘 가장 자주 느끼는 감정은 무엇인가요?", "감정"},
            new Object[]{"지금 이 순간 마음 속에 무거운 것이 있다면 무엇인가요?", "감정"},
            new Object[]{"최근 감사함을 느낀 일이 있나요?", "감정"},

            new Object[]{"오늘 가장 만나고 싶은 사람이 있다면 누구인가요? 이유는요?", "관계"},
            new Object[]{"최근 누군가에게 고마움을 전했나요?", "관계"},
            new Object[]{"지금 내 삶에서 가장 소중한 사람은 누구인가요?", "관계"},
            new Object[]{"누군가와 갈등이 있다면 어떻게 풀고 싶나요?", "관계"},
            new Object[]{"혼자 있는 시간과 함께하는 시간 중 요즘 어느 쪽이 더 그립나요?", "관계"},

            new Object[]{"오늘 하루를 시작하며 가장 기대되는 것은 무엇인가요?", "일상"},
            new Object[]{"어제 하루 중 잘 된 일 한 가지를 떠올려보세요.", "일상"},
            new Object[]{"오늘 꼭 해야 할 일 중 가장 중요한 것은 무엇인가요?", "일상"},
            new Object[]{"지금 일상에서 작은 즐거움을 주는 것들은 무엇인가요?", "일상"},
            new Object[]{"최근 루틴에서 바꾸고 싶은 것이 있나요?", "일상"},

            new Object[]{"올해 꼭 이루고 싶은 목표 한 가지는 무엇인가요?", "성장"},
            new Object[]{"최근 새롭게 배운 것이 있나요?", "성장"},
            new Object[]{"지금의 나에게 가장 필요한 것은 무엇이라고 생각하나요?", "성장"},
            new Object[]{"1년 후의 내 모습을 상상하면 어떤 감정이 드나요?", "성장"},
            new Object[]{"어떤 면에서 어제보다 조금 성장했다고 느끼나요?", "성장"},

            new Object[]{"오늘 몸 상태는 어떤가요?", "건강"},
            new Object[]{"요즘 충분히 쉬고 있나요?", "건강"},
            new Object[]{"지금 스트레스를 받고 있다면 그 원인이 무엇인가요?", "건강"},
            new Object[]{"몸과 마음을 위해 최근 한 가장 좋은 선택은 무엇인가요?", "건강"},
            new Object[]{"오늘 자신을 위해 할 수 있는 작은 친절은 무엇일까요?", "건강"}
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        long count = questionRepository.count();
        if (count > 0) {
            log.info("질문 데이터가 이미 존재합니다. ({} 개) 초기화를 건너뜁니다.", count);
            return;
        }

        List<Question> questions = INITIAL_QUESTIONS.stream()
                .map(row -> Question.create((String) row[0], (String) row[1]))
                .toList();

        questionRepository.saveAll(questions);
        log.info("초기 질문 {} 개를 저장했습니다.", questions.size());
    }
}
