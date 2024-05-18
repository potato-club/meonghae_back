package com.meonghae.userservice.core.exception.impl;

import com.meonghae.userservice.core.exception.ErrorCode;

public class UnsupportedJwtException extends BusinessException {

    public UnsupportedJwtException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
