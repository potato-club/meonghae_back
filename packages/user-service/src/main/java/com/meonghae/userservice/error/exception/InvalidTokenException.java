package com.meonghae.userservice.error.exception;


import com.meonghae.userservice.error.ErrorCode;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
