package com.capstone.domain.journal.repository;

import com.capstone.domain.journal.entity.JournalEmotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalEmotionRepository extends JpaRepository<JournalEmotion, Long> {

  @Modifying
  @Query("""
      delete from JournalEmotion je
      where je.journalAnalysis.id in (
          select ja.id from JournalAnalysis ja
          join ja.journal j
          where j.user.id = :userId
      )
      """)
  void deleteByUserId(@Param("userId") Long userId);
}
