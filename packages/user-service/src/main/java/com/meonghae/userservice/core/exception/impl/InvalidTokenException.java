package com.meonghae.userservice.core.exception.impl;


import com.meonghae.userservice.core.exception.ErrorCode;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
