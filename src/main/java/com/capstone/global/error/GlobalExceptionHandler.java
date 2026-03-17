package com.capstone.global.error;

import com.capstone.global.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
      MethodArgumentNotValidException.class,
      BindException.class,
      ConstraintViolationException.class,
      HttpMessageNotReadableException.class
  })
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e) {
    log.warn("Bad request exception type={}", e.getClass().getSimpleName());
    log.debug("Bad request exception detail", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ApiResponse.fail(ErrorStatus.BAD_REQUEST)
    );
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
    return ResponseEntity.status(e.getErrorStatus().getHttpStatus()).body(
        ApiResponse.fail(e.getErrorStatus())
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    log.error("Unhandled exception occurred", e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        ApiResponse.fail(ErrorStatus.INTERNAL_SERVER_ERROR)
    );
  }
}