package com.moenghae.apigatewayservice.config;

import com.moenghae.apigatewayservice.jwt.JwtTokenProvider;
import com.moenghae.apigatewayservice.jwt.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>
        implements Ordered {

    JwtTokenProvider jwtTokenProvider;
    RedisService redisService;

    public AuthorizationHeaderFilter(JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    @Override
    public int getOrder() {
        return -2;  // -1 is response write filter, must be called before that
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String path = request.getURI().getPath();
            String androidId = request.getHeaders().getFirst("androidId");
            String accessToken = jwtTokenProvider.resolveAccessToken(request);
            String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

            if (isPublicPath(path)) {
                return chain.filter(exchange.mutate().request(
                        request.mutate().header("androidId", androidId).build()
                ).build());
            }

            if (accessToken == null) {
                if (jwtTokenProvider.validateToken(refreshToken) && redisService.isRefreshTokenValid(refreshToken)
                        && redisService.isAndroidIdValid(refreshToken, androidId)) {
                    return chain.filter(exchange.mutate().request(
                            request.mutate()
                                    .header("androidId", androidId)
                                    .header("RefreshToken", refreshToken)
                                    .build()).build());
                }
            } else {
                if (jwtTokenProvider.validateToken(accessToken) && !redisService.isTokenInBlacklist(accessToken)) {
                    log.info("JWT Token is good.");
                }
            }

            return chain.filter(exchange.mutate().request(
                    request.mutate()
                            .header(HttpHeaders.AUTHORIZATION, accessToken)
                            .header("RefreshToken", refreshToken)
                            .build()).build());
        });
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/health") || path.endsWith("/prometheus") ||
                path.contains("/signup") || path.contains("/login") ||
                path.contains("/swagger-ui/index.html") || path.startsWith("/user-service/users") ||
                path.contains("/cancel") || path.contains("/check") || path.contains("/send/token");
    }
}
