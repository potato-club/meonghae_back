package com.meonghae.userservice.core.exception.impl;

import com.meonghae.userservice.core.exception.ErrorCode;

public class JwtExpiredException extends BusinessException {

    public JwtExpiredException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
