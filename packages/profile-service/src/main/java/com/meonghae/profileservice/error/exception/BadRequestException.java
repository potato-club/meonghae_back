package com.meonghae.profileservice.error.exception;

import com.meonghae.profileservice.error.ErrorCode;

public class BadRequestException extends BusinessException{

    BadRequestException(String message, ErrorCode errorCode) {
        super(errorCode, message);
    }
}
