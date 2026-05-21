package com.capstone.domain.question.repository;

import com.capstone.domain.question.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {

    boolean existsByDailyQuestionId(Long dailyQuestionId);
}
