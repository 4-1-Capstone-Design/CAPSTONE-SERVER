package com.capstone.domain.user.service;

import com.capstone.domain.auth.repository.RefreshTokenRepository;
import com.capstone.domain.clover.repository.CloverHistoryRepository;
import com.capstone.domain.journal.repository.JournalAnalysisRepository;
import com.capstone.domain.journal.repository.JournalEmotionRepository;
import com.capstone.domain.journal.repository.JournalKeywordRepository;
import com.capstone.domain.journal.repository.JournalReplyRepository;
import com.capstone.domain.journal.repository.JournalRepository;
import com.capstone.domain.question.repository.DailyQuestionRepository;
import com.capstone.domain.question.repository.QuestionAnswerKeywordRepository;
import com.capstone.domain.question.repository.QuestionAnswerRepository;
import com.capstone.domain.user.dto.request.WithdrawRequestDto;
import com.capstone.domain.user.dto.response.MyPageResponseDto;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JournalEmotionRepository journalEmotionRepository;
  private final JournalKeywordRepository journalKeywordRepository;
  private final JournalAnalysisRepository journalAnalysisRepository;
  private final JournalReplyRepository journalReplyRepository;
  private final JournalRepository journalRepository;
  private final CloverHistoryRepository cloverHistoryRepository;
  private final QuestionAnswerKeywordRepository questionAnswerKeywordRepository;
  private final QuestionAnswerRepository questionAnswerRepository;
  private final DailyQuestionRepository dailyQuestionRepository;

  public User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));
  }

  public MyPageResponseDto getMyPage(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

    return new MyPageResponseDto(
        user.getEmail(),
        user.getNickname(),
        user.getCloverBalance(),
        resolveCloverComment(user.getCloverBalance())
    );
  }

  @Transactional
  public void withdrawUser(Long userId, WithdrawRequestDto request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new BusinessException(ErrorStatus.INVALID_PASSWORD);
    }

    journalEmotionRepository.deleteByUserId(userId);
    journalKeywordRepository.deleteByUserId(userId);
    journalAnalysisRepository.deleteByUserId(userId);
    journalReplyRepository.deleteByUserId(userId);
    journalRepository.deleteByUserId(userId);

    cloverHistoryRepository.deleteByUserId(userId);

    questionAnswerKeywordRepository.deleteByUserId(userId);
    questionAnswerRepository.deleteByUserId(userId);
    dailyQuestionRepository.deleteByUserId(userId);

    refreshTokenRepository.deleteById(userId);

    userRepository.deleteById(userId);
  }

  private String resolveCloverComment(Long clover) {
    if (clover == null || clover == 0) return "아직 첫 저널을 기다리고 있어요!";
    if (clover <= 3)   return "새싹 모닝저널러!";
    if (clover <= 7)   return "성장 중인 모닝저널러!";
    if (clover <= 14)  return "꾸준한 모닝저널러!";
    if (clover <= 30)  return "열혈 모닝저널러!";
    if (clover <= 60)  return "모닝저널 고수!";
    if (clover <= 100) return "전설의 모닝저널러!";
    return "모닝저널 레전드!";
  }
}