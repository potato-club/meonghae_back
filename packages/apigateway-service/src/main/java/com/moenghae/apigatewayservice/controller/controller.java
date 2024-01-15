package com.moenghae.apigatewayservice.controller;

import com.moenghae.apigatewayservice.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class controller {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/health-check")
    public String check() {
        return "health-checking";
    }

    @GetMapping("/check")
    public boolean checkToken(ServerHttpRequest request) {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        return jwtTokenProvider.validateToken(accessToken);
    }
}
