package com.meonghae.userservice.error.exception;

import com.meonghae.userservice.error.ErrorCode;

public class SignatureException extends BusinessException {

    public SignatureException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
