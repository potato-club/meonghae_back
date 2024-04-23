package com.meonghae.userservice.core.exception.impl;

import com.meonghae.userservice.core.exception.ErrorCode;

public class IllegalArgumentException extends BusinessException {

    public IllegalArgumentException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
