package com.capstone.domain.journal.repository;

import com.capstone.domain.journal.entity.JournalReply;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalReplyRepository extends JpaRepository<JournalReply, Long> {
  Optional<JournalReply> findTopByJournalIdOrderByCreatedAtDesc(Long journalId);
  boolean existsByJournalId(Long journalId);

  @Modifying
  @Query("""
      delete from JournalReply jr
      where jr.journal.id in (
          select j.id from Journal j where j.user.id = :userId
      )
      """)
  void deleteByUserId(@Param("userId") Long userId);
}