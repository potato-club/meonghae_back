package com.moenghae.apigatewayservice.error.jwt;

import com.moenghae.apigatewayservice.error.ErrorCode;

public class UnsupportedJwtException extends BusinessException {

    public UnsupportedJwtException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
