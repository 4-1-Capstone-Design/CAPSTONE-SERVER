package com.capstone.global.error;

import lombok.Getter;

import java.util.Objects;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorStatus errorStatus;

  public BusinessException(ErrorStatus errorStatus) {
    super(Objects.requireNonNull(errorStatus, "ErrorStatus must not be null").getMessage());
    this.errorStatus = errorStatus;
  }
}