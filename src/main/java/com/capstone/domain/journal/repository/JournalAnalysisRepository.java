package com.capstone.domain.journal.repository;

import com.capstone.domain.journal.entity.JournalAnalysis;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalAnalysisRepository extends JpaRepository<JournalAnalysis, Long> {
  Optional<JournalAnalysis> findTopByJournalIdOrderByAnalyzedAtDesc(Long journalId);

  @Modifying
  @Query("""
      delete from JournalAnalysis ja
      where ja.journal.id in (
          select j.id from Journal j where j.user.id = :userId
      )
      """)
  void deleteByUserId(@Param("userId") Long userId);
}