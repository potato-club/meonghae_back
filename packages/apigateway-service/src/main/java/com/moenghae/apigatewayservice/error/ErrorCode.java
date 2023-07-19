package com.moenghae.apigatewayservice.error;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode {
    INVALID_JWT_TOKEN(4001, "4001", "Invalid JWT token"),
    JWT_TOKEN_EXPIRED(4002, "4002","JWT token has expired"),
    UNSUPPORTED_JWT_TOKEN(4003, "4003","JWT token is unsupported"),
    EMPTY_JWT_CLAIMS(4004, "4004","JWT claims string is empty"),
    JWT_SIGNATURE_MISMATCH(4005, "4005","JWT signature does not match");

    private final int code;
    private final String status;
    private final String message;

    ErrorCode(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
