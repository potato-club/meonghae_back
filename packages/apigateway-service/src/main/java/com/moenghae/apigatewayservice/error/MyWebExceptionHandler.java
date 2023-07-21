package com.moenghae.apigatewayservice.error;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.SignatureException;

public class MyWebExceptionHandler implements ErrorWebExceptionHandler {

    private String errorCodeMaker(String errorMessage) {
        return "{\"errorMessage\":" + errorMessage + "}";
    }

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange, Throwable ex) {

        String errorMessage = "JWT Error";

        if (ex.getClass() == MalformedJwtException.class) {
            errorMessage = ErrorCode.INVALID_JWT_TOKEN.getMessage();
        } else if (ex.getClass() == UnsupportedJwtException.class) {
            errorMessage = ErrorCode.UNSUPPORTED_JWT_TOKEN.getMessage();
        } else if (ex.getClass() == ExpiredJwtException.class) {
            errorMessage = ErrorCode.JWT_TOKEN_EXPIRED.getMessage();
        } else if (ex.getClass() == IllegalArgumentException.class) {
            errorMessage = ErrorCode.EMPTY_JWT_CLAIMS.getMessage();
        } else if (ex.getClass() == SignatureException.class) {
            errorMessage = ErrorCode.JWT_SIGNATURE_MISMATCH.getMessage();
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        byte[] bytes = errorCodeMaker(errorMessage).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}
