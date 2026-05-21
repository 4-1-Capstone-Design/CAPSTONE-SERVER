package com.capstone.domain.question.repository;

import com.capstone.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findAllByIsActiveTrue();

    List<Question> findAllByCategoryAndIsActiveTrue(String category);

    boolean existsByContent(String content);
}
