package com.meonghae.userservice.error.exception;

import com.meonghae.userservice.error.ErrorCode;

public class IllegalArgumentException extends BusinessException {

    public IllegalArgumentException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
