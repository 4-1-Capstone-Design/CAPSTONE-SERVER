package com.capstone.domain.keyword.repository;

import com.capstone.domain.keyword.entity.Keyword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
  Optional<Keyword> findByName(String name);
}