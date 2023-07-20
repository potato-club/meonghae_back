package com.moenghae.apigatewayservice.config;

import com.moenghae.apigatewayservice.error.ErrorCode;
import com.moenghae.apigatewayservice.error.jwt.*;
import com.moenghae.apigatewayservice.error.jwt.IllegalArgumentException;
import com.moenghae.apigatewayservice.jwt.JwtTokenProvider;
import com.moenghae.apigatewayservice.jwt.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
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
            ServerHttpResponse response = exchange.getResponse();

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
                    handleTokenValidationFailure(response, e);
                    return response.setComplete();
                }
            } else {
                try {
                    if (jwtTokenProvider.validateToken(accessToken) && !redisService.isTokenInBlacklist(accessToken)) {
                        log.info("JWT Token is good.");
                    }
                } catch (RuntimeException e) {
                    handleTokenValidationFailure(response, e);
                    return response.setComplete();
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
        return path.endsWith("/health") || path.endsWith("/prometheus") ||
                path.startsWith("/user-service/signup") || path.startsWith("/user-service/login") ||
                path.endsWith("/swagger-ui/index.html") || path.startsWith("/s3-file-service/files/users");
    }

    private void handleTokenValidationFailure(ServerHttpResponse response, RuntimeException e) {
        HttpStatus status = null;
        ErrorCode errorCode = null;

        if (e instanceof InvalidTokenException) {
            status = HttpStatus.UNAUTHORIZED;
            errorCode = ErrorCode.INVALID_JWT_TOKEN;
        } else if (e instanceof JwtExpiredException) {
            status = HttpStatus.UNAUTHORIZED;
            errorCode = ErrorCode.JWT_TOKEN_EXPIRED;
        } else if (e instanceof UnsupportedJwtException) {
            status = HttpStatus.UNAUTHORIZED;
            errorCode = ErrorCode.UNSUPPORTED_JWT_TOKEN;
        } else if (e instanceof IllegalArgumentException){
            status = HttpStatus.UNAUTHORIZED;
            errorCode = ErrorCode.EMPTY_JWT_CLAIMS;
        } else if (e instanceof SignatureException) {
            status = HttpStatus.UNAUTHORIZED;
            errorCode = ErrorCode.JWT_SIGNATURE_MISMATCH;
        }

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String errorMessage = errorCode.getMessage();
        response.writeAndFlushWith(Mono.just(Mono.just(response.bufferFactory().wrap(errorMessage.getBytes()))));
    }
}
