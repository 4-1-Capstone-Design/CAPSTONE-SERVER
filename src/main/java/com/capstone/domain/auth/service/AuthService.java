package com.capstone.domain.auth.service;

import com.capstone.domain.auth.dto.request.LoginRequestDto;
import com.capstone.domain.auth.dto.request.LogoutRequestDto;
import com.capstone.domain.auth.dto.request.SignUpRequestDto;
import com.capstone.domain.auth.dto.response.LoginResponseDto;
import com.capstone.domain.auth.dto.response.SignUpResponseDto;
import com.capstone.domain.auth.entity.RefreshToken;
import com.capstone.domain.auth.repository.RefreshTokenRepository;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
import com.capstone.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Value("${jwt.refresh-token-validity}")
  private long refreshTokenValidity; // ms 단위라고 가정

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
      throw new BusinessException(ErrorStatus.BAD_REQUEST);
    }

    return SignUpResponseDto.builder()
        .userId(savedUser.getId())
        .email(savedUser.getEmail())
        .nickname(savedUser.getNickname())
        .build();
  }

  @Transactional
  public LoginResponseDto login(LoginRequestDto request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new BusinessException(ErrorStatus.INVALID_PASSWORD);
    }

    user.updateUpdatedAt();

    String accessToken = jwtTokenProvider.createAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

    long ttlSeconds = refreshTokenValidity / 1000;

    refreshTokenRepository.save(
        RefreshToken.builder()
            .userId(user.getId())
            .refreshToken(refreshToken)
            .ttl(ttlSeconds)
            .build()
    );

    return LoginResponseDto.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Transactional
  public void logout(LogoutRequestDto request) {
    Long userId = jwtTokenProvider.getUserIdFromToken(request.refreshToken());

    RefreshToken refreshToken = refreshTokenRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND));

    if (!refreshToken.getRefreshToken().equals(request.refreshToken())) {
      throw new BusinessException(ErrorStatus.INVALID_REFRESH_TOKEN);
    }

    refreshTokenRepository.deleteById(userId);
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