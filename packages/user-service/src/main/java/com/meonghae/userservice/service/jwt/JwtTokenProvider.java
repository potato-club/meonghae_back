package com.meonghae.userservice.service.jwt;

import com.meonghae.userservice.domin.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JwtTokenProvider {

    String createAccessToken(String email, UserRole userRole, String androidId);

    String createRefreshToken(String email, UserRole userRole, String androidId);

    UsernamePasswordAuthenticationToken getAuthentication(String token);

    String getUserEmail(String token);

    String resolveAccessToken(HttpServletRequest request);

    String resolveRefreshToken(HttpServletRequest request);

    String reissueAccessToken(String refreshToken, String androidId);

    String reissueRefreshToken(String refreshToken, String accessToken, String androidId);

    void expireToken(String token);

    boolean validateToken(String jwtToken);

    void setHeaderAccessToken(HttpServletResponse response, String accessToken);

    void setHeaderRefreshToken(HttpServletResponse response, String refreshToken);
}
