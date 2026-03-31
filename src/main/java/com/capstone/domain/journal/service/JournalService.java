package com.capstone.domain.journal.service;

import com.capstone.domain.journal.dto.request.JournalCreateRequestDto;
import com.capstone.domain.journal.dto.response.JournalCreateResponseDto;
import com.capstone.domain.journal.entity.Journal;
import com.capstone.domain.journal.repository.JournalRepository;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JournalService {

  private final JournalRepository journalRepository;
  private final UserRepository userRepository;

  @Transactional
  public JournalCreateResponseDto createJournal(Long userId, JournalCreateRequestDto request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

    if (journalRepository.existsByUserIdAndJournalDateAndIsDeletedFalse(userId, request.journalDate())) {
      throw new BusinessException(ErrorStatus.JOURNAL_ALREADY_EXISTS_TODAY);
    }

    Journal journal = Journal.create(
        request.title(),
        request.content(),
        request.journalDate(),
        user
    );

    Journal savedJournal = journalRepository.save(journal);

    return JournalCreateResponseDto.builder()
        .journalId(savedJournal.getId())
        .title(savedJournal.getTitle())
        .content(savedJournal.getContent())
        .journalDate(savedJournal.getJournalDate())
        .createdAt(savedJournal.getCreatedAt())
        .userId(user.getId())
        .build();
  }
}