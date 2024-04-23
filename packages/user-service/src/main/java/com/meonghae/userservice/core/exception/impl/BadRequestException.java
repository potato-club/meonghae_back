package com.meonghae.userservice.core.exception.impl;

import com.meonghae.userservice.core.exception.ErrorCode;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
