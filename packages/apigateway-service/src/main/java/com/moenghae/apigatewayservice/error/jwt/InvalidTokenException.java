package com.moenghae.apigatewayservice.error.jwt;

import com.moenghae.apigatewayservice.error.ErrorCode;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
