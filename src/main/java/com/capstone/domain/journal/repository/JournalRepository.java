package com.capstone.domain.journal.repository;

import com.capstone.domain.journal.entity.Journal;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalRepository extends JpaRepository<Journal, Long> {
  boolean existsByUserIdAndJournalDateAndIsDeletedFalse(Long userId, LocalDate journalDate);
  Optional<Journal> findByIdAndIsDeletedFalse(Long journalId);
  Optional<Journal> findByUserIdAndJournalDateAndIsDeletedFalse(Long userId, LocalDate journalDate);
}