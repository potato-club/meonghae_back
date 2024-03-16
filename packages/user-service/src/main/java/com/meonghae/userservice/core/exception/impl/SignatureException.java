package com.meonghae.userservice.core.exception.impl;

import com.meonghae.userservice.core.exception.ErrorCode;

public class SignatureException extends BusinessException {

    public SignatureException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
