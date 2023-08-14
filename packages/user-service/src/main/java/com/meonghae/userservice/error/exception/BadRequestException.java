package com.meonghae.userservice.error.exception;

import com.meonghae.userservice.error.ErrorCode;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
