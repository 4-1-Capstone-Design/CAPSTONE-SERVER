package com.capstone.domain.journal.repository;

import com.capstone.domain.journal.entity.JournalKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JournalKeywordRepository extends JpaRepository<JournalKeyword, Long> {

  @Query("""
      select jk.keyword.name, count(jk.id), sum(jk.score)
      from JournalKeyword jk
      join jk.journalAnalysis ja
      join ja.journal j
      where j.user.id = :userId
        and j.isDeleted = false
        and year(j.journalDate) = :year
        and month(j.journalDate) = :month
      group by jk.keyword.name
      """)
  List<Object[]> findMonthlyKeywordStats(
      @Param("userId") Long userId,
      @Param("year") int year,
      @Param("month") int month
  );
}