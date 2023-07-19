package com.meonghae.userservice.error.jwt;

public class CustomJwtException extends RuntimeException {
    private final ErrorJwtCode errorCode;

    public CustomJwtException(ErrorJwtCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode.getCode();
    }
}
