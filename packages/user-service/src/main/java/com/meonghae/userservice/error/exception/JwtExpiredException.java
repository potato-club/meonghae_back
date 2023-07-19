package com.meonghae.userservice.error.exception;

import com.meonghae.userservice.error.ErrorCode;

public class JwtExpiredException extends BusinessException {

    public JwtExpiredException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
