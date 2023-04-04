package com.meonghae.userservice.jwt;

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

        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String path = request.getRequestURI();

        if (path.equals("/login") || path.equals("/signup")) {
            filterChain.doFilter(request, response);
        }

        if (accessToken == null) {
            String newAccessToken = jwtTokenProvider.resolveAccessToken(response);
            if (jwtTokenProvider.validateToken(newAccessToken)) {
                this.setAuthentication(newAccessToken);
            }
        } else {
            this.setAuthentication(accessToken);
        }

        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String token) {
        // 토큰으로부터 유저 정보를 받아옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // SecurityContext 에 Authentication 객체를 저장합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
