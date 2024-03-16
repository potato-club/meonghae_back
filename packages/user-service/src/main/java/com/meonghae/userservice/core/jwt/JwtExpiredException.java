package com.meonghae.userservice.core.jwt;

import org.springframework.security.core.AuthenticationException;

public class JwtExpiredException extends AuthenticationException {
    public JwtExpiredException(String message) {
        super(message);
    }
}
