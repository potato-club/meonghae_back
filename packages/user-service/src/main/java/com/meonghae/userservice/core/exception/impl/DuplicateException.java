package com.meonghae.userservice.core.exception.impl;


import com.meonghae.userservice.core.exception.ErrorCode;

public class DuplicateException extends BusinessException {

    public DuplicateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
