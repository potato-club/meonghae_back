package com.meonghae.userservice.error.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorJwtCode {
    INVALID_JWT_TOKEN(4001, "Invalid JWT token"),
    JWT_TOKEN_EXPIRED(4002, "JWT token has expired"),
    UNSUPPORTED_JWT_TOKEN(4003, "JWT token is unsupported"),
    EMPTY_JWT_CLAIMS(4004, "JWT claims string is empty"),
    JWT_SIGNATURE_MISMATCH(4005, "JWT signature does not match");

    private final int code;
    private final String message;
}
