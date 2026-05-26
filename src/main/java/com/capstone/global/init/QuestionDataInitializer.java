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
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionDataInitializer implements ApplicationRunner {

    private final QuestionRepository questionRepository;

    private static final List<Object[]> INITIAL_QUESTIONS = List.of(
            // ── 감정 (30개) ──────────────────────────────────────────────
            new Object[]{"오늘 아침 눈을 떴을 때 첫 번째로 든 감정은 무엇인가요?", "감정"},
            new Object[]{"최근 가장 행복했던 순간을 떠올려보세요. 무엇이 그 감정을 만들었나요?", "감정"},
            new Object[]{"요즘 가장 자주 느끼는 감정은 무엇인가요?", "감정"},
            new Object[]{"지금 이 순간 마음 속에 무거운 것이 있다면 무엇인가요?", "감정"},
            new Object[]{"최근 감사함을 느낀 일이 있나요?", "감정"},
            new Object[]{"오늘 내 감정에 색을 붙인다면 어떤 색인가요? 왜 그런가요?", "감정"},
            new Object[]{"최근 눈물이 날 것 같았던 순간이 있었나요?", "감정"},
            new Object[]{"지금 마음이 불편하다면, 그 이유를 말로 표현해볼 수 있나요?", "감정"},
            new Object[]{"최근 가장 설레었던 일은 무엇인가요?", "감정"},
            new Object[]{"감정을 억누르고 있는 부분이 있나요?", "감정"},
            new Object[]{"오늘 기분에 가장 큰 영향을 준 것은 무엇인가요?", "감정"},
            new Object[]{"최근 이유 없이 공허한 느낌이 든 적 있나요?", "감정"},
            new Object[]{"지금 내 마음 상태를 날씨로 표현한다면 어떤가요?", "감정"},
            new Object[]{"분노나 짜증을 느꼈을 때 어떻게 해소하나요?", "감정"},
            new Object[]{"오늘 스스로에게 가장 솔직할 수 있는 감정 한 가지는 무엇인가요?", "감정"},
            new Object[]{"최근 가장 두려운 것은 무엇인가요?", "감정"},
            new Object[]{"어떤 상황에서 가장 불안함을 느끼나요?", "감정"},
            new Object[]{"최근 뿌듯했던 순간을 떠올려보세요.", "감정"},
            new Object[]{"지금 마음에 걸리는 것이 있나요? 구체적으로 무엇인가요?", "감정"},
            new Object[]{"기분이 좋을 때와 나쁠 때, 각각 어떤 행동을 하게 되나요?", "감정"},
            new Object[]{"오늘 하루 중 가장 평온했던 순간은 언제인가요?", "감정"},
            new Object[]{"나를 가장 힘들게 하는 감정은 무엇이고, 어떻게 다루나요?", "감정"},
            new Object[]{"지금 이 순간 안도감을 느끼는 일이 있나요?", "감정"},
            new Object[]{"최근 예상치 못한 감정을 경험한 적이 있나요?", "감정"},
            new Object[]{"오늘 나에게 가장 필요한 감정적 지지는 무엇인가요?", "감정"},
            new Object[]{"최근 감정이 롤러코스터처럼 변한 날이 있었나요?", "감정"},
            new Object[]{"지금 내가 느끼는 감정을 누군가에게 솔직하게 말할 수 있나요?", "감정"},
            new Object[]{"어떤 감정을 표현하기 가장 어려운가요?", "감정"},
            new Object[]{"오늘 무언가에 기대가 되는 것이 있나요?", "감정"},
            new Object[]{"최근 작은 일에도 크게 반응했다면, 그 이유가 무엇일까요?", "감정"},

            // ── 관계 (30개) ──────────────────────────────────────────────
            new Object[]{"오늘 가장 만나고 싶은 사람이 있다면 누구인가요? 이유는요?", "관계"},
            new Object[]{"최근 누군가에게 고마움을 전했나요?", "관계"},
            new Object[]{"지금 내 삶에서 가장 소중한 사람은 누구인가요?", "관계"},
            new Object[]{"누군가와 갈등이 있다면 어떻게 풀고 싶나요?", "관계"},
            new Object[]{"혼자 있는 시간과 함께하는 시간 중 요즘 어느 쪽이 더 그립나요?", "관계"},
            new Object[]{"최근 누군가의 말이나 행동에서 힘을 얻은 적이 있나요?", "관계"},
            new Object[]{"지금 더 많은 시간을 함께하고 싶은 사람이 있나요?", "관계"},
            new Object[]{"누군가에게 하고 싶었는데 아직 못 한 말이 있나요?", "관계"},
            new Object[]{"최근 친구나 가족과 의미 있는 대화를 나눈 적이 있나요?", "관계"},
            new Object[]{"나를 있는 그대로 받아들여주는 사람이 내 삶에 있나요?", "관계"},
            new Object[]{"최근 관계에서 상처받은 일이 있나요?", "관계"},
            new Object[]{"내가 먼저 연락하고 싶은 사람이 있나요?", "관계"},
            new Object[]{"요즘 가장 외롭다고 느낄 때는 언제인가요?", "관계"},
            new Object[]{"나에게 가장 솔직하게 말해주는 사람은 누구인가요?", "관계"},
            new Object[]{"최근 새로운 사람을 만나거나 관계가 시작된 적이 있나요?", "관계"},
            new Object[]{"내가 먼저 사과해야 할 사람이 있나요?", "관계"},
            new Object[]{"주변 사람들에게 나는 어떤 사람으로 보이고 싶나요?", "관계"},
            new Object[]{"관계에서 가장 중요하게 생각하는 것은 무엇인가요?", "관계"},
            new Object[]{"최근 감사하다는 말을 직접 전하지 못한 사람이 있나요?", "관계"},
            new Object[]{"누군가와 함께 하고 싶은 일이 있다면 무엇인가요?", "관계"},
            new Object[]{"내가 힘들 때 가장 먼저 떠오르는 사람은 누구인가요?", "관계"},
            new Object[]{"최근 오해가 생긴 관계가 있나요?", "관계"},
            new Object[]{"나는 주변 사람들에게 어떤 존재이고 싶나요?", "관계"},
            new Object[]{"최근 누군가와의 관계에서 변화를 느꼈나요?", "관계"},
            new Object[]{"지금 가장 응원하고 싶은 사람은 누구인가요?", "관계"},
            new Object[]{"내가 관계에서 자주 반복하는 패턴이 있다면 무엇인가요?", "관계"},
            new Object[]{"최근 함께해서 좋았던 순간을 떠올려보세요.", "관계"},
            new Object[]{"혼자 해결하기 어려운 고민을 누군가와 나눠본 적 있나요?", "관계"},
            new Object[]{"나의 경계선을 지키지 못했던 경험이 있나요?", "관계"},
            new Object[]{"지금 내 삶에 새로운 인연이 필요하다고 느끼나요?", "관계"},

            // ── 일상 (30개) ──────────────────────────────────────────────
            new Object[]{"오늘 하루를 시작하며 가장 기대되는 것은 무엇인가요?", "일상"},
            new Object[]{"어제 하루 중 잘 된 일 한 가지를 떠올려보세요.", "일상"},
            new Object[]{"오늘 꼭 해야 할 일 중 가장 중요한 것은 무엇인가요?", "일상"},
            new Object[]{"지금 일상에서 작은 즐거움을 주는 것들은 무엇인가요?", "일상"},
            new Object[]{"최근 루틴에서 바꾸고 싶은 것이 있나요?", "일상"},
            new Object[]{"오늘 아침 나를 기분 좋게 만든 작은 것이 있었나요?", "일상"},
            new Object[]{"오늘 하루 중 온전히 나를 위한 시간이 있었나요?", "일상"},
            new Object[]{"최근 새로 생긴 습관이나 루틴이 있나요?", "일상"},
            new Object[]{"요즘 가장 자주 가는 공간은 어디인가요? 그곳이 나에게 어떤 의미인가요?", "일상"},
            new Object[]{"오늘 밥은 맛있게 먹었나요? 나 자신을 잘 챙기고 있나요?", "일상"},
            new Object[]{"요즘 가장 자주 듣는 음악이나 즐기는 콘텐츠는 무엇인가요?", "일상"},
            new Object[]{"오늘 하루를 한 단어로 표현한다면 무엇인가요?", "일상"},
            new Object[]{"최근 우연히 기분이 좋아진 순간이 있었나요?", "일상"},
            new Object[]{"오늘 자신에게 작은 선물을 한다면 무엇을 하고 싶나요?", "일상"},
            new Object[]{"요즘 아침 루틴은 어떤가요? 만족스럽나요?", "일상"},
            new Object[]{"최근 '이건 잘하고 있다'고 느끼는 일상 속 습관이 있나요?", "일상"},
            new Object[]{"오늘 하루 중 가장 집중했던 순간은 언제인가요?", "일상"},
            new Object[]{"요즘 주말을 어떻게 보내나요? 만족스러운가요?", "일상"},
            new Object[]{"지금 당장 하고 싶은 것이 있다면 무엇인가요?", "일상"},
            new Object[]{"최근 일상에서 변화를 주고 싶은 것이 있나요?", "일상"},
            new Object[]{"오늘 밖에 나갔나요? 오늘의 날씨는 어떻게 느껴졌나요?", "일상"},
            new Object[]{"요즘 나에게 활력을 주는 것은 무엇인가요?", "일상"},
            new Object[]{"오늘 가장 오래 한 일은 무엇인가요?", "일상"},
            new Object[]{"최근 미뤄두고 있는 일이 있나요?", "일상"},
            new Object[]{"내일을 기대하게 만드는 것이 있나요?", "일상"},
            new Object[]{"요즘 자주 웃게 만드는 것이 있나요?", "일상"},
            new Object[]{"최근 새로운 곳에 가보거나 새로운 것을 시도해봤나요?", "일상"},
            new Object[]{"지금 하고 싶은 것을 마음껏 할 수 있다면 무엇을 하겠나요?", "일상"},
            new Object[]{"요즘 내 하루에서 가장 기다려지는 시간은 언제인가요?", "일상"},
            new Object[]{"일상에서 당연하게 여기지만 사실은 소중한 것이 있다면 무엇인가요?", "일상"},

            // ── 성장 (30개) ──────────────────────────────────────────────
            new Object[]{"올해 꼭 이루고 싶은 목표 한 가지는 무엇인가요?", "성장"},
            new Object[]{"최근 새롭게 배운 것이 있나요?", "성장"},
            new Object[]{"지금의 나에게 가장 필요한 것은 무엇이라고 생각하나요?", "성장"},
            new Object[]{"1년 후의 내 모습을 상상하면 어떤 감정이 드나요?", "성장"},
            new Object[]{"어떤 면에서 어제보다 조금 성장했다고 느끼나요?", "성장"},
            new Object[]{"지금 가장 두려운 도전은 무엇인가요? 그 도전을 해보고 싶나요?", "성장"},
            new Object[]{"내가 포기한 것 중 다시 시도해보고 싶은 것이 있나요?", "성장"},
            new Object[]{"오늘 나 자신에게 칭찬할 수 있는 한 가지는 무엇인가요?", "성장"},
            new Object[]{"나를 성장시켜준 실패나 어려움이 있다면 무엇인가요?", "성장"},
            new Object[]{"지금 내가 가장 잘하고 있다고 느끼는 것은 무엇인가요?", "성장"},
            new Object[]{"최근 나 자신에 대해 새롭게 알게 된 것이 있나요?", "성장"},
            new Object[]{"10년 후의 내가 지금의 나에게 해줄 말은 무엇일까요?", "성장"},
            new Object[]{"지금 가장 개선하고 싶은 나의 습관은 무엇인가요?", "성장"},
            new Object[]{"나는 어떤 사람이 되고 싶나요?", "성장"},
            new Object[]{"최근 나를 자극하거나 영감을 준 것이 있나요?", "성장"},
            new Object[]{"지금 나에게 가장 부족하다고 느끼는 것은 무엇인가요?", "성장"},
            new Object[]{"내가 꿈꾸는 삶의 모습은 어떤가요?", "성장"},
            new Object[]{"최근 스스로에게 너무 엄격하게 대한 적이 있나요?", "성장"},
            new Object[]{"잘 하고 싶지만 아직 시작하지 못한 것이 있나요?", "성장"},
            new Object[]{"나의 강점을 최대한 활용하고 있나요?", "성장"},
            new Object[]{"지금 배우고 있거나 배우고 싶은 것이 있나요?", "성장"},
            new Object[]{"나는 실패를 어떻게 받아들이나요?", "성장"},
            new Object[]{"최근 내 기준이나 가치관이 바뀐 것이 있나요?", "성장"},
            new Object[]{"지금 나를 가장 성장하게 만드는 환경은 무엇인가요?", "성장"},
            new Object[]{"올해 가장 잘한 결정은 무엇인가요?", "성장"},
            new Object[]{"아직 내 안에서 발견하지 못한 잠재력이 있다고 생각하나요?", "성장"},
            new Object[]{"지금 나에게 필요한 용기는 무엇인가요?", "성장"},
            new Object[]{"나를 발전시켜주는 사람이나 환경은 무엇인가요?", "성장"},
            new Object[]{"최근 나 자신을 한계로 느끼게 한 순간이 있었나요?", "성장"},
            new Object[]{"지금의 나를 1년 전의 나와 비교하면 어떤 점이 달라졌나요?", "성장"},

            // ── 건강 (30개) ──────────────────────────────────────────────
            new Object[]{"오늘 몸 상태는 어떤가요?", "건강"},
            new Object[]{"요즘 충분히 쉬고 있나요?", "건강"},
            new Object[]{"지금 스트레스를 받고 있다면 그 원인이 무엇인가요?", "건강"},
            new Object[]{"몸과 마음을 위해 최근 한 가장 좋은 선택은 무엇인가요?", "건강"},
            new Object[]{"오늘 자신을 위해 할 수 있는 작은 친절은 무엇일까요?", "건강"},
            new Object[]{"요즘 잠을 잘 자고 있나요? 수면의 질은 어떤가요?", "건강"},
            new Object[]{"마지막으로 야외에서 신선한 공기를 마신 게 언제인가요?", "건강"},
            new Object[]{"몸이 보내는 신호 중 최근 무시하고 있는 것이 있나요?", "건강"},
            new Object[]{"요즘 무엇이 나를 가장 지치게 하나요?", "건강"},
            new Object[]{"지금 내가 가장 필요로 하는 휴식의 형태는 무엇인가요?", "건강"},
            new Object[]{"오늘 몸을 움직인 적이 있나요?", "건강"},
            new Object[]{"최근 식사 패턴은 어떤가요? 규칙적으로 먹고 있나요?", "건강"},
            new Object[]{"지금 내 몸이 나에게 보내는 가장 큰 신호는 무엇인가요?", "건강"},
            new Object[]{"요즘 가장 자주 느끼는 신체적 불편함이 있나요?", "건강"},
            new Object[]{"마음이 지칠 때 나만의 회복 방법은 무엇인가요?", "건강"},
            new Object[]{"최근 자신을 돌보는 일을 소홀히 한 적이 있나요?", "건강"},
            new Object[]{"수면의 질이 삶의 질에 어떤 영향을 주고 있나요?", "건강"},
            new Object[]{"요즘 물을 충분히 마시고 있나요?", "건강"},
            new Object[]{"최근 번아웃의 징후를 느낀 적이 있나요?", "건강"},
            new Object[]{"스트레스 해소를 위해 주로 어떤 방법을 쓰나요?", "건강"},
            new Object[]{"오늘 내 몸에게 고마운 것이 있다면 무엇인가요?", "건강"},
            new Object[]{"나의 에너지를 가장 많이 소모시키는 것은 무엇인가요?", "건강"},
            new Object[]{"요즘 나만의 힐링 방법은 무엇인가요?", "건강"},
            new Object[]{"최근 몸과 마음 중 어느 쪽이 더 피곤한가요?", "건강"},
            new Object[]{"건강을 위해 하고 싶지만 아직 못 하고 있는 것이 있나요?", "건강"},
            new Object[]{"오늘 잠깐 멈추고 깊게 숨을 쉰 적이 있나요?", "건강"},
            new Object[]{"최근 몸 상태가 기분에 영향을 미친 적이 있나요?", "건강"},
            new Object[]{"지금 내 몸에 가장 필요한 것은 무엇인가요?", "건강"},
            new Object[]{"나는 아플 때 스스로를 어떻게 돌보나요?", "건강"},
            new Object[]{"건강한 하루를 보냈다고 느끼는 나만의 기준이 있나요?", "건강"}
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        long count = questionRepository.count();
        if (count >= INITIAL_QUESTIONS.size()) {
            log.info("질문 데이터가 최신 상태입니다. ({} 개)", count);
            return;
        }

        Set<String> existingContents = questionRepository.findAll().stream()
                .map(Question::getContent)
                .collect(Collectors.toSet());

        List<Question> toAdd = INITIAL_QUESTIONS.stream()
                .filter(row -> !existingContents.contains((String) row[0]))
                .map(row -> Question.create((String) row[0], (String) row[1]))
                .toList();

        questionRepository.saveAll(toAdd);
        log.info("질문 {} 개 추가 완료. (총 {} 개)", toAdd.size(), count + toAdd.size());
    }
}
