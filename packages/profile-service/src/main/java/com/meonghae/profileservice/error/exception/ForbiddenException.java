package com.meonghae.profileservice.error.exception;

import com.meonghae.profileservice.error.ErrorCode;
import lombok.Getter;

@Getter
public class ForbiddenException extends BusinessException {
  public ForbiddenException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
} // 나랑 코드 다르게짰낭 나잠만 아빠전화
