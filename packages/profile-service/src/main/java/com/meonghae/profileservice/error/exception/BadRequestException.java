package com.meonghae.profileservice.error.exception;

import com.meonghae.profileservice.error.ErrorCode;
import lombok.Getter;

@Getter
public class BadRequestException extends BusinessException {

  public BadRequestException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
