package com.capstone.domain.question.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question_answer")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_question_id", nullable = false, unique = true)
    private DailyQuestion dailyQuestion;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 읽기 전용 매핑 — 저장은 QuestionAnswerKeywordRepository로 직접 수행
    @OneToMany(mappedBy = "questionAnswer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionAnswerKeyword> keywords = new ArrayList<>();

    public static QuestionAnswer create(DailyQuestion dailyQuestion, String content) {
        return QuestionAnswer.builder()
                .dailyQuestion(dailyQuestion)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
