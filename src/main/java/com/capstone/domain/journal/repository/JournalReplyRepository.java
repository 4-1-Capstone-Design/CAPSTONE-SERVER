package com.capstone.domain.journal.repository;

import com.capstone.domain.journal.entity.JournalReply;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalReplyRepository extends JpaRepository<JournalReply, Long> {
  Optional<JournalReply> findTopByJournalIdOrderByCreatedAtDesc(Long journalId);
  boolean existsByJournalId(Long journalId);
}