package com.capstone.domain.clover.service;

import com.capstone.domain.clover.dto.response.CloverBalanceResponseDto;
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
public class CloverService {

  private final UserRepository userRepository;

  public CloverBalanceResponseDto getCloverBalance(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

    return CloverBalanceResponseDto.builder()
        .cloverBalance(user.getCloverBalance())
        .build();
  }
}