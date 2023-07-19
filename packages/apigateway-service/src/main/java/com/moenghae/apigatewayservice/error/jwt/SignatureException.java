package com.moenghae.apigatewayservice.error.jwt;

import com.moenghae.apigatewayservice.error.ErrorCode;

public class SignatureException extends BusinessException {

    public SignatureException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
