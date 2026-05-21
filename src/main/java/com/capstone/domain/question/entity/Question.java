package com.capstone.domain.question.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String category;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public static Question create(String content, String category) {
        return Question.builder()
                .content(content)
                .category(category)
                .isActive(true)
                .build();
    }
}
