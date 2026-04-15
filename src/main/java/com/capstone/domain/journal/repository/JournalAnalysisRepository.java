package com.capstone.domain.journal.repository;

import com.capstone.domain.journal.entity.JournalAnalysis;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalAnalysisRepository extends JpaRepository<JournalAnalysis, Long> {
  Optional<JournalAnalysis> findTopByJournalIdOrderByAnalyzedAtDesc(Long journalId);
}