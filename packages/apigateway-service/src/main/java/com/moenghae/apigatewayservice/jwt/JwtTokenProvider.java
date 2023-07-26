package com.moenghae.apigatewayservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class JwtTokenProvider {

    // 키
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenValidTime;

    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenValidTime;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct // 의존성 주입 후, 초기화를 수행
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Access Token 생성.
    public String createAccessToken(String email, String roles) {
        return this.createToken(email, roles, accessTokenValidTime);
    }

    public String createRefreshToken(String email, String roles) {
        return this.createToken(email, roles, refreshTokenValidTime);
    }

    // Create token
    public String createToken(String email, String roles, long tokenValid) {
        Claims claims = Jwts.claims().setSubject(email); // claims 생성 및 payload 설정
        claims.put("roles", roles); // 권한 설정, key/ value 쌍으로 저장

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Date date = new Date();

        return Jwts.builder()
                .setClaims(claims) // 발행 유저 정보 저장
                .setIssuedAt(date) // 발행 시간 저장
                .setExpiration(new Date(date.getTime() + tokenValid)) // 토큰 유효 시간 저장
                .signWith(key, SignatureAlgorithm.HS256) // 해싱 알고리즘 및 키 설정
                .compact(); // 생성
    }

    // Request의 Header에서 AccessToken 값을 가져옵니다. "Authorization" : "token"
    public String resolveAccessToken(ServerHttpRequest request) {
        List<String> authorizationHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String authorizationHeader = authorizationHeaders.get(0);
            if (authorizationHeader.startsWith("Bearer ")) {
                return authorizationHeader.substring(7);
            }
        }
        return null;
    }

    // Request의 Header에서 RefreshToken 값을 가져옵니다. "refreshToken" : "token"
    public String resolveRefreshToken(ServerHttpRequest request) {
        if(request.getHeaders().containsKey("refreshToken"))
            return request.getHeaders().get("refreshToken").get(0).substring(7);
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwtToken);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "Token has expired");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims string is empty");
        } catch (SignatureException e) {
            throw new SignatureException("JWT signature does not match");
        }
    }
}
