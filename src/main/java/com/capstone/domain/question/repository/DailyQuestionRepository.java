package com.capstone.domain.question.repository;

import com.capstone.domain.question.entity.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Long> {

    List<DailyQuestion> findAllByUserIdAndQuestionDateOrderByDisplayOrder(Long userId, LocalDate date);

    Optional<DailyQuestion> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT dq.question.id FROM DailyQuestion dq WHERE dq.user.id = :userId AND dq.questionDate >= :since")
    List<Long> findRecentQuestionIdsByUserId(@Param("userId") Long userId, @Param("since") LocalDate since);

    @Query("""
            SELECT dq.question.category, COUNT(dq.id)
            FROM DailyQuestion dq
            JOIN dq.answer a
            WHERE dq.user.id = :userId
            AND dq.questionDate >= :since
            GROUP BY dq.question.category
            """)
    List<Object[]> countAnsweredByCategory(@Param("userId") Long userId, @Param("since") LocalDate since);

    @Query("""
            SELECT COUNT(dq) FROM DailyQuestion dq
            JOIN dq.answer a
            WHERE dq.user.id = :userId
              AND YEAR(dq.questionDate) = :year
              AND MONTH(dq.questionDate) = :month
            """)
    long countAnsweredByUserAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    @Modifying
    @Query("delete from DailyQuestion dq where dq.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
