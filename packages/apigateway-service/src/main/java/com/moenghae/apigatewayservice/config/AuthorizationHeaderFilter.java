package com.moenghae.apigatewayservice.config;

import com.moenghae.apigatewayservice.jwt.JwtTokenProvider;
import com.moenghae.apigatewayservice.jwt.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

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

            if (path.endsWith("/health") || path.endsWith("/prometheus") || path.startsWith("/user-service/signup") ||
                    path.startsWith("/user-service/login") || path.endsWith("/swagger-ui/index.html") ||
                    path.startsWith("/s3-file-service/files/users")) {

                return chain.filter(exchange.mutate().request(
                        request.mutate()
                                .header("androidId", androidId)         // AndroidId 헤더 추가
                                .build()
                ).build());
            }

            String accessToken = jwtTokenProvider.resolveAccessToken(request);
            String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

            if (accessToken == null) {
                if (jwtTokenProvider.validateToken(refreshToken) && redisService.isRefreshTokenValid(refreshToken)
                  && redisService.isAndroidIdValid(refreshToken)) {
                    List<String> tokenList = jwtTokenProvider.reissueToken(refreshToken, androidId);
                    accessToken = tokenList.get(0);
                    refreshToken = tokenList.get(1);
                }
            } else if (accessToken != null) {
                if (jwtTokenProvider.validateToken(accessToken) && !redisService.isTokenInBlacklist(accessToken)) {
                    log.info("JWT Token is good.");
                }
            }

            return chain.filter(exchange.mutate().request(
                    request.mutate()
                            .header(HttpHeaders.AUTHORIZATION, accessToken)     // Authorization 헤더 추가
                            .header("refreshToken", refreshToken)   // refreshToken 헤더 추가
                            .build()
            ).build());
        });
    }
}
