package com.capstone.domain.journal.repository;

import com.capstone.domain.journal.entity.Journal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalRepository extends JpaRepository<Journal, Long> {
  boolean existsByUserIdAndJournalDateAndIsDeletedFalse(Long userId, LocalDate journalDate);
  Optional<Journal> findByIdAndIsDeletedFalse(Long journalId);
  Optional<Journal> findByUserIdAndJournalDateAndIsDeletedFalse(Long userId, LocalDate journalDate);

  @Query("""
      select j
      from Journal j
      where j.user.id = :userId
        and j.isDeleted = false
        and (:cursor is null or j.id < :cursor)
      order by j.id desc
      """)
  List<Journal> findAllByUserIdWithCursor(
      @Param("userId") Long userId,
      @Param("cursor") Long cursor,
      Pageable pageable
  );

  @Query("""
      select count(j) from Journal j
      where j.user.id = :userId
        and j.isDeleted = false
        and year(j.journalDate) = :year
        and month(j.journalDate) = :month
      """)
  long countByUserIdAndMonth(
      @Param("userId") Long userId,
      @Param("year") int year,
      @Param("month") int month
  );

  @Modifying
  @Query("delete from Journal j where j.user.id = :userId")
  void deleteByUserId(@Param("userId") Long userId);
}