package com.meonghae.userservice.error.exception;

import com.meonghae.userservice.error.ErrorCode;

public class UnsupportedJwtException extends BusinessException {

    public UnsupportedJwtException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
