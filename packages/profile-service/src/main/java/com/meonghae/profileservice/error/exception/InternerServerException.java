package com.meonghae.profileservice.error.exception;

import com.meonghae.profileservice.error.ErrorCode;

public class InternerServerException extends BusinessException {
  public InternerServerException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
