package com.capstone.domain.journal.service;

import com.capstone.domain.journal.dto.request.JournalCreateRequestDto;
import com.capstone.domain.journal.dto.response.*;
import com.capstone.domain.journal.entity.*;
import com.capstone.domain.journal.repository.*;
import com.capstone.domain.keyword.entity.Keyword;
import com.capstone.domain.keyword.repository.KeywordRepository;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JournalService {

  private static final int MAX_REPLY_SOURCE_LENGTH = 1000;

  private final JournalRepository journalRepository;
  private final UserRepository userRepository;
  private final JournalReplyRepository journalReplyRepository;
  private final JournalAnalysisRepository journalAnalysisRepository;
  private final JournalKeywordRepository journalKeywordRepository;
  private final KeywordRepository keywordRepository;
  private final OpenAiJournalAiService openAiJournalAiService;

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

    Long nextCursor = hasNext ? journals.get(journals.size() - 1).getId() : null;

    return JournalCursorResponseDto.builder()
        .journals(journalList)
        .nextCursor(nextCursor)
        .hasNext(hasNext)
        .build();
  }

  @Transactional
  public JournalAnalyzeResponseDto createJournalReply(Long userId, Long journalId) {

    Journal journal = journalRepository.findByIdAndIsDeletedFalse(journalId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.JOURNAL_NOT_FOUND));

    if (!journal.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorStatus.FORBIDDEN_USER);
    }

    JournalAnalysis existingAnalysis = journalAnalysisRepository
        .findTopByJournalIdOrderByAnalyzedAtDesc(journalId)
        .orElse(null);

    if (existingAnalysis != null) {
      return mapToAnalyzeResponse(journalId, existingAnalysis);
    }

    String truncatedContent = truncateJournalContent(journal.getContent());
    JournalAiResultDto aiResult =
        openAiJournalAiService.generateAnalysisResult(truncatedContent);

    JournalAnalysis savedAnalysis = journalAnalysisRepository.save(
        JournalAnalysis.create(
            aiResult.summary(),
            openAiJournalAiService.getModel(),
            "v1",
            journal
        )
    );

    boolean exists = journalReplyRepository
        .findTopByJournalIdOrderByCreatedAtDesc(journalId)
        .isPresent();

    if (!exists) {
      journalReplyRepository.save(
          JournalReply.create(
              aiResult.reply(),
              openAiJournalAiService.getModel(),
              journal
          )
      );
    }

    List<JournalKeyword> savedKeywords = saveKeywords(savedAnalysis, aiResult.keywords());

    return mapToAnalyzeResponse(journalId, savedAnalysis, savedKeywords);
  }

  private List<JournalKeyword> saveKeywords(JournalAnalysis analysis,
      List<JournalAiResultDto.KeywordItem> keywords) {
    List<JournalKeyword> saved = new ArrayList<>();

    for (JournalAiResultDto.KeywordItem item : keywords) {
      Keyword keyword = keywordRepository.findByName(item.name())
          .orElseGet(() -> keywordRepository.save(
              Keyword.builder()
                  .name(item.name())
                  .build()
          ));

      JournalKeyword jk = journalKeywordRepository.save(
          JournalKeyword.builder()
              .score(item.score())
              .journalAnalysis(analysis)
              .keyword(keyword)
              .build()
      );
      saved.add(jk);
    }

    return saved;
  }

  public List<JournalKeywordItemDto> getKeywords(Long userId, Long journalId) {

    Journal journal = journalRepository.findByIdAndIsDeletedFalse(journalId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.JOURNAL_NOT_FOUND));

    if (!journal.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorStatus.FORBIDDEN_USER);
    }

    JournalAnalysis analysis = journalAnalysisRepository
        .findTopByJournalIdOrderByAnalyzedAtDesc(journalId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.JOURNAL_NOT_FOUND));

    return analysis.getJournalKeywords().stream()
        .map(journalKeyword -> JournalKeywordItemDto.builder()
            .keyword(journalKeyword.getKeyword().getName())
            .score(journalKeyword.getScore())
            .build())
        .toList();
  }

  private JournalAnalyzeResponseDto mapToAnalyzeResponse(Long journalId, JournalAnalysis analysis) {

    List<JournalKeywordItemDto> keywordItems = analysis.getJournalKeywords().stream()
        .map(journalKeyword -> JournalKeywordItemDto.builder()
            .keyword(journalKeyword.getKeyword().getName())
            .score(journalKeyword.getScore())
            .build())
        .toList();

    String reply = journalReplyRepository.findTopByJournalIdOrderByCreatedAtDesc(journalId)
        .map(JournalReply::getContent)
        .orElse(null);

    return JournalAnalyzeResponseDto.builder()
        .journalId(journalId)
        .summary(analysis.getSummary())
        .reply(reply)
        .keywords(keywordItems)
        .build();
  }

  private JournalAnalyzeResponseDto mapToAnalyzeResponse(Long journalId, JournalAnalysis analysis,
      List<JournalKeyword> keywords) {

    List<JournalKeywordItemDto> keywordItems = keywords.stream()
        .map(jk -> JournalKeywordItemDto.builder()
            .keyword(jk.getKeyword().getName())
            .score(jk.getScore())
            .build())
        .toList();

    String reply = journalReplyRepository.findTopByJournalIdOrderByCreatedAtDesc(journalId)
        .map(JournalReply::getContent)
        .orElse(null);

    return JournalAnalyzeResponseDto.builder()
        .journalId(journalId)
        .summary(analysis.getSummary())
        .reply(reply)
        .keywords(keywordItems)
        .build();
  }

  private String truncateJournalContent(String content) {
    if (content == null || content.isBlank()) {
      throw new BusinessException(ErrorStatus.JOURNAL_CONTENT_EMPTY);
    }

    return content.length() <= MAX_REPLY_SOURCE_LENGTH
        ? content
        : content.substring(0, MAX_REPLY_SOURCE_LENGTH);
  }
}