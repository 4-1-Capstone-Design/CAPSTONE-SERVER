package com.capstone.domain.journal.service;

import com.capstone.domain.journal.dto.request.JournalCreateRequestDto;
import com.capstone.domain.journal.dto.response.JournalCreateResponseDto;
import com.capstone.domain.journal.dto.response.JournalCursorResponseDto;
import com.capstone.domain.journal.dto.response.JournalGetResponseDto;
import com.capstone.domain.journal.dto.response.JournalListItemResponseDto;
import com.capstone.domain.journal.dto.response.JournalReplyResponseDto;
import com.capstone.domain.journal.entity.Journal;
import com.capstone.domain.journal.entity.JournalReply;
import com.capstone.domain.journal.repository.JournalReplyRepository;
import com.capstone.domain.journal.repository.JournalRepository;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JournalService {

  private static final int MAX_REPLY_SOURCE_LENGTH = 1000;
  private static final String REPLY_MODEL_NAME = "gpt-5.4";

  private final JournalRepository journalRepository;
  private final UserRepository userRepository;
  private final JournalReplyRepository journalReplyRepository;
  private final OpenAiReplyService openAiReplyService;

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

  @Transactional
  public void deleteJournal(Long userId, Long journalId) {
    Journal journal = journalRepository.findByIdAndIsDeletedFalse(journalId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.JOURNAL_NOT_FOUND));

    if (!journal.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorStatus.FORBIDDEN_USER);
    }

    journal.delete();
  }

  @Transactional(readOnly = true)
  public JournalGetResponseDto getJournalByDate(Long userId, LocalDate date) {
    Journal journal = journalRepository
        .findByUserIdAndJournalDateAndIsDeletedFalse(userId, date)
        .orElseThrow(() -> new BusinessException(ErrorStatus.JOURNAL_NOT_FOUND));

    return JournalGetResponseDto.builder()
        .journalId(journal.getId())
        .title(journal.getTitle())
        .content(journal.getContent())
        .journalDate(journal.getJournalDate())
        .createdAt(journal.getCreatedAt())
        .build();
  }

  @Transactional(readOnly = true)
  public JournalCursorResponseDto getJournalList(Long userId, Long cursor, int size) {
    int pageSize = Math.max(1, Math.min(size, 50));

    List<Journal> journals = journalRepository.findAllByUserIdWithCursor(
        userId,
        cursor,
        PageRequest.of(0, pageSize + 1)
    );

    boolean hasNext = journals.size() > pageSize;

    if (hasNext) {
      journals = journals.subList(0, pageSize);
    }

    List<JournalListItemResponseDto> journalList = journals.stream()
        .map(journal -> JournalListItemResponseDto.builder()
            .journalId(journal.getId())
            .title(journal.getTitle())
            .content(journal.getContent())
            .journalDate(journal.getJournalDate())
            .createdAt(journal.getCreatedAt())
            .build())
        .toList();

    Long nextCursor = null;
    if (hasNext && !journals.isEmpty()) {
      nextCursor = journals.get(journals.size() - 1).getId();
    }

    return JournalCursorResponseDto.builder()
        .journals(journalList)
        .nextCursor(nextCursor)
        .hasNext(hasNext)
        .build();
  }

  @Transactional
  public JournalReplyResponseDto createJournalReply(Long userId, Long journalId) {
    Journal journal = journalRepository.findByIdAndIsDeletedFalse(journalId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.JOURNAL_NOT_FOUND));

    if (!journal.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorStatus.FORBIDDEN_USER);
    }

    JournalReply existingReply = journalReplyRepository
        .findTopByJournalIdOrderByCreatedAtDesc(journalId)
        .orElse(null);

    if (existingReply != null) {
      return JournalReplyResponseDto.builder()
          .replyId(existingReply.getId())
          .journalId(journal.getId())
          .content(existingReply.getContent())
          .modelName(existingReply.getModelName())
          .createdAt(existingReply.getCreatedAt())
          .build();
    }

    String truncatedContent = truncateJournalContent(journal.getContent());
    String aiReply = openAiReplyService.generateReply(truncatedContent);

    JournalReply savedReply = journalReplyRepository.save(
        JournalReply.create(aiReply, REPLY_MODEL_NAME, journal)
    );

    return JournalReplyResponseDto.builder()
        .replyId(savedReply.getId())
        .journalId(journal.getId())
        .content(savedReply.getContent())
        .modelName(savedReply.getModelName())
        .createdAt(savedReply.getCreatedAt())
        .build();
  }

  private String truncateJournalContent(String content) {
    if (content == null || content.isBlank()) {
      throw new BusinessException(ErrorStatus.JOURNAL_NOT_FOUND);
    }

    if (content.length() <= MAX_REPLY_SOURCE_LENGTH) {
      return content;
    }

    return content.substring(0, MAX_REPLY_SOURCE_LENGTH);
  }
}