package com.moenghae.apigatewayservice.error;

import com.moenghae.apigatewayservice.jwt.JwtExpiredException;
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

    private String errorCodeMaker(int errorCode) {
        return "{\"errorCode\":" + errorCode + "}";
    }

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange, Throwable ex) {

        int errorCode = 4006;

        if (ex.getClass() == MalformedJwtException.class) {
            errorCode = ErrorCode.INVALID_JWT_TOKEN.getCode();
        } else if (ex.getClass() == UnsupportedJwtException.class) {
            errorCode = ErrorCode.UNSUPPORTED_JWT_TOKEN.getCode();
        } else if (ex.getClass() == JwtExpiredException.class) {
            errorCode = ErrorCode.JWT_TOKEN_EXPIRED.getCode();
        } else if (ex.getClass() == IllegalArgumentException.class) {
            errorCode = ErrorCode.EMPTY_JWT_CLAIMS.getCode();
        } else if (ex.getClass() == SignatureException.class) {
            errorCode = ErrorCode.JWT_SIGNATURE_MISMATCH.getCode();
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        byte[] bytes = errorCodeMaker(errorCode).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}
