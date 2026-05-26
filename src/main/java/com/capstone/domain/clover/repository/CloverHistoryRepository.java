package com.capstone.domain.clover.repository;

import com.capstone.domain.clover.entity.CloverHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CloverHistoryRepository extends JpaRepository<CloverHistory, Long> {

  @Modifying
  @Query("delete from CloverHistory ch where ch.user.id = :userId")
  void deleteByUserId(@Param("userId") Long userId);
}
