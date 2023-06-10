package com.meonghae.userservice.error.exception;


import com.meonghae.userservice.error.ErrorCode;

public class DuplicateException extends BusinessException {

    public DuplicateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
