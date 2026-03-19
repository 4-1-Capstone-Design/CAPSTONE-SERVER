package com.capstone.domain.user.service;

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
public class UserService {

  private final UserRepository userRepository;

  public User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));
  }
}