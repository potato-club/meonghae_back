package com.moenghae.apigatewayservice.config;

import com.moenghae.apigatewayservice.jwt.JwtTokenProvider;
import com.moenghae.apigatewayservice.jwt.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    Environment env;
    JwtTokenProvider jwtTokenProvider;
    RedisService redisService;

    public AuthorizationHeaderFilter(Environment env, JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        super(Config.class);
        this.env = env;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            String path = request.getURI().getPath();

            if (path.startsWith("/user-service/signup") || path.startsWith("/user-service/login")) {
                return chain.filter(exchange);
            }

            String accessToken = jwtTokenProvider.resolveAccessToken(request);

            if (accessToken == null) {
                String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
                if (jwtTokenProvider.validateToken(refreshToken) && redisService.isRefreshTokenValid(refreshToken, ipAddress)) {
                    String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken, ipAddress);
                    jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
                }
            } else if (accessToken != null) {
                if (jwtTokenProvider.validateToken(accessToken) && !redisService.isTokenInBlacklist(accessToken)) {
                    return chain.filter(exchange);
                }
            }

            return chain.filter(exchange);
        });
    }

    // Mono, Flux -> Spring WebFlux
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }
}
