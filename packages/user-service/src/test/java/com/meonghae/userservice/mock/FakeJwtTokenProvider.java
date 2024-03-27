package com.meonghae.userservice.mock;

import com.meonghae.userservice.domin.user.enums.UserRole;
import com.meonghae.userservice.service.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FakeJwtTokenProvider implements JwtTokenProvider {



    @Override
    public String createAccessToken(String email, UserRole userRole, String androidId) {
        return null;
    }

    @Override
    public String createRefreshToken(String email, UserRole userRole, String androidId) {
        return null;
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        return null;
    }

    @Override
    public String getUserEmail(String token) {
        return null;
    }

    @Override
    public String resolveAccessToken(HttpServletRequest request) {
        return null;
    }

    @Override
    public String resolveRefreshToken(HttpServletRequest request) {
        return null;
    }

    @Override
    public String reissueAccessToken(String refreshToken, String androidId) {
        return null;
    }

    @Override
    public String reissueRefreshToken(String refreshToken, String accessToken, String androidId) {
        return null;
    }

    @Override
    public void expireToken(String token) {

    }

    @Override
    public boolean validateToken(String jwtToken) {
        return false;
    }

    @Override
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {

    }

    @Override
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {

    }
}
