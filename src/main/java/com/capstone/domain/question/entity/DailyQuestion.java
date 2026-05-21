package com.capstone.domain.question.entity;

import com.capstone.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "daily_question",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_daily_question_per_user", columnNames = {"user_id", "question_date", "question_id"}),
        @UniqueConstraint(name = "uq_daily_order_per_user", columnNames = {"user_id", "question_date", "display_order"})
    }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "question_date", nullable = false)
    private LocalDate questionDate;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @OneToOne(mappedBy = "dailyQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private QuestionAnswer answer;

    public static DailyQuestion create(User user, Question question, LocalDate date, int order) {
        return DailyQuestion.builder()
                .user(user)
                .question(question)
                .questionDate(date)
                .displayOrder(order)
                .build();
    }
}
