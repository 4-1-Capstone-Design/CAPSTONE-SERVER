package com.capstone.domain.journal.repository;

import com.capstone.domain.journal.entity.JournalKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalKeywordRepository extends JpaRepository<JournalKeyword, Long> {
}