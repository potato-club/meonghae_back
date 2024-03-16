package com.meonghae.userservice.core.exception.impl;


import com.meonghae.userservice.core.exception.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
