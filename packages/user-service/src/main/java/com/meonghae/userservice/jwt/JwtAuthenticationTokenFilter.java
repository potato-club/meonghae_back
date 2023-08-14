package com.meonghae.userservice.jwt;

import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.contains("/swagger") || path.contains("/v2/api-docs") || path.endsWith("/prometheus")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.contains("/login") || path.equals("/signup") || path.contains("/users") || path.contains("/cancel")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if (accessToken == null && jwtTokenProvider.validateToken(refreshToken)) {
            filterChain.doFilter(request, response);
            return;
        } else if (jwtTokenProvider.validateToken(accessToken)){
            this.setAuthentication(accessToken);
        } else {
            throw new MalformedJwtException("Invalid JWT Token");
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token) {
        // 토큰으로부터 유저 정보를 받아옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // SecurityContext 에 Authentication 객체를 저장합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
