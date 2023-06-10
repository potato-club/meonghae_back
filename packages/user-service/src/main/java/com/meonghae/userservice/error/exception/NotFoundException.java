package com.meonghae.userservice.error.exception;


import com.meonghae.userservice.error.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
