package com.capstone.domain.user.service;

import com.capstone.domain.user.dto.request.SignUpRequestDto;
import com.capstone.domain.user.dto.response.SignUpResponseDto;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public SignUpResponseDto signUp(SignUpRequestDto request) {
    validateDuplicateEmail(request.email());
    validateDuplicateNickname(request.nickname());

    User user = User.create(
        request.email(),
        passwordEncoder.encode(request.password()),
        request.nickname()
    );

    User savedUser;
    try {
      savedUser = userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      if (userRepository.existsByEmail(request.email())) {
        throw new BusinessException(ErrorStatus.EMAIL_ALREADY_EXISTS);
      }
      if (userRepository.existsByNickname(request.nickname())) {
        throw new BusinessException(ErrorStatus.NICKNAME_ALREADY_EXISTS);
      }
      throw e;
    }
    return SignUpResponseDto.builder()
        .userId(savedUser.getId())
        .email(savedUser.getEmail())
        .nickname(savedUser.getNickname())
        .build();
  }

  private void validateDuplicateEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorStatus.EMAIL_ALREADY_EXISTS);
    }
  }

  private void validateDuplicateNickname(String nickname) {
    if (userRepository.existsByNickname(nickname)) {
      throw new BusinessException(ErrorStatus.NICKNAME_ALREADY_EXISTS);
    }
  }
}