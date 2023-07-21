package com.moenghae.apigatewayservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moenghae.apigatewayservice.error.ErrorCode;
import com.moenghae.apigatewayservice.error.ErrorResponse;
import com.moenghae.apigatewayservice.jwt.JwtTokenProvider;
import com.moenghae.apigatewayservice.jwt.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    JwtTokenProvider jwtTokenProvider;
    RedisService redisService;

    public AuthorizationHeaderFilter(JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String path = request.getURI().getPath();
            String androidId = request.getHeaders().getFirst("AndroidId");
            String accessToken = jwtTokenProvider.resolveAccessToken(request);
            String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

            if (isPublicPath(path)) {
                return chain.filter(exchange.mutate().request(
                        request.mutate().header("androidId", androidId).build()
                ).build());
            }

            if (accessToken == null) {
                try {
                    if (jwtTokenProvider.validateToken(refreshToken) && redisService.isRefreshTokenValid(refreshToken)
                            && redisService.isAndroidIdValid(refreshToken)) {
                        List<String> tokenList = jwtTokenProvider.reissueToken(refreshToken, androidId);
                        accessToken = tokenList.get(0);
                        refreshToken = tokenList.get(1);
                    }
                } catch (RuntimeException e) {
                    return handleTokenValidationFailure(exchange, e);
                }
            } else {
                try {
                    if (jwtTokenProvider.validateToken(accessToken) && !redisService.isTokenInBlacklist(accessToken)) {
                        log.info("JWT Token is good.");
                    }
                } catch (RuntimeException e) {
                    return handleTokenValidationFailure(exchange, e);
                }
            }

            return chain.filter(exchange.mutate().request(
                    request.mutate()
                            .header(HttpHeaders.AUTHORIZATION, accessToken)
                            .header("refreshToken", refreshToken)
                            .build()
            ).build());
        });
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/health") || path.endsWith("/prometheus") ||
                path.startsWith("/user-service/signup") || path.startsWith("/user-service/login") ||
                path.endsWith("/swagger-ui/index.html");
    }

    private Mono<Void> handleTokenValidationFailure(ServerWebExchange exchange, RuntimeException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;;
        ErrorCode errorCode = ErrorCode.INVALID_JWT_TOKEN;

        if (e instanceof ExpiredJwtException) {
            errorCode = ErrorCode.JWT_TOKEN_EXPIRED;
        } else if (e instanceof UnsupportedJwtException) {
            errorCode = ErrorCode.UNSUPPORTED_JWT_TOKEN;
        } else if (e instanceof IllegalArgumentException) {
            errorCode = ErrorCode.EMPTY_JWT_CLAIMS;
        } else if (e instanceof SignatureException) {
            errorCode = ErrorCode.JWT_SIGNATURE_MISMATCH;
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorMessage(errorCode.getMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        String errorMessageJson;

        try {
            errorMessageJson = objectMapper.writeValueAsString(errorResponse);
        } catch (JsonProcessingException ex) {
            // JSON 변환 오류가 발생할 경우에 대한 예외 처리
            errorMessageJson = "{\"errorCode\":\"INTERNAL_SERVER_ERROR\",\"errorMessage\":\"Failed to process the request.\"}";
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorMessageJson.getBytes());
        exchange.getResponse().setComplete(); // Set the response as complete
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
