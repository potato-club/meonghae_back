package com.moenghae.apigatewayservice.error.jwt;

import com.moenghae.apigatewayservice.error.ErrorCode;

public class IllegalArgumentException extends BusinessException {

    public IllegalArgumentException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
