package com.moenghae.apigatewayservice.error.jwt;

import com.moenghae.apigatewayservice.error.ErrorCode;

public class JwtExpiredException extends BusinessException {

    public JwtExpiredException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
