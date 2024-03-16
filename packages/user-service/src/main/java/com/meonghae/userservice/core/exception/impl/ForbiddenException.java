package com.meonghae.userservice.core.exception.impl;


import com.meonghae.userservice.core.exception.ErrorCode;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
