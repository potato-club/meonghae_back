package com.meonghae.profileservice.error.exception;

import com.meonghae.profileservice.error.ErrorCode;
import lombok.Getter;

@Getter
public class UnAuthorizedException extends BusinessException {
  public UnAuthorizedException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
