package com.moenghae.apigatewayservice.error;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
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

        int errorCode = 401;

        if (ex.getClass() == MalformedJwtException.class) {
            errorCode = 4001;
        } else if (ex.getClass() == UnsupportedJwtException.class) {
            errorCode = 4002;
        } else if (ex.getClass() == ExpiredJwtException.class) {
            errorCode = 4003;
        } else if (ex.getClass() == IllegalArgumentException.class) {
            errorCode = 4004;
        } else if (ex.getClass() == SignatureException.class) {
            errorCode = 4005;
        }

        byte[] bytes = errorCodeMaker(errorCode).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}
