package com.capstone.domain.question.repository;

import com.capstone.domain.question.entity.QuestionAnswerKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionAnswerKeywordRepository extends JpaRepository<QuestionAnswerKeyword, Long> {

    @Query("""
            select qak.keyword.name, count(qak.id), sum(qak.score)
            from QuestionAnswerKeyword qak
            join qak.questionAnswer qa
            join qa.dailyQuestion dq
            where dq.user.id = :userId
              and year(dq.questionDate) = :year
              and month(dq.questionDate) = :month
            group by qak.keyword.name
            """)
    List<Object[]> findMonthlyKeywordStats(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );
}
