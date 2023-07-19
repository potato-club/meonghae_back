package com.moenghae.apigatewayservice.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCode {
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED,"4001", "Invalid JWT token"),
    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "4002", "JWT token has expired"),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "4003", "JWT token is unsupported"),
    EMPTY_JWT_CLAIMS(HttpStatus.UNAUTHORIZED, "4004", "JWT claims string is empty"),
    JWT_SIGNATURE_MISMATCH(HttpStatus.UNAUTHORIZED, "4005", "JWT signature does not match");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
