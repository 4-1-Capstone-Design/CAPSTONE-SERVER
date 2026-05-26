package com.capstone.domain.question.repository;

import com.capstone.domain.question.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {

    boolean existsByDailyQuestionId(Long dailyQuestionId);

    @Modifying
    @Query("""
        delete from QuestionAnswer qa
        where qa.dailyQuestion.id in (
            select dq.id from DailyQuestion dq where dq.user.id = :userId
        )
        """)
    void deleteByUserId(@Param("userId") Long userId);
}
