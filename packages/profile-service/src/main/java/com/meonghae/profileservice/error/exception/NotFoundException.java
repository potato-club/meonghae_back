package com.meonghae.profileservice.error.exception;
import com.meonghae.profileservice.error.ErrorCode;

public class NotFoundException extends BusinessException{
    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
