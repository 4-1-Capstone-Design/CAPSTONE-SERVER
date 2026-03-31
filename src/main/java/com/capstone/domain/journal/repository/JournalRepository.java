package com.capstone.domain.journal.repository;

import com.capstone.domain.journal.entity.Journal;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalRepository extends JpaRepository<Journal, Long> {
  boolean existsByUserIdAndJournalDateAndIsDeletedFalse(Long userId, LocalDate journalDate);
}