package com.meonghae.userservice.jwt;

import com.meonghae.userservice.enums.UserRole;
import com.meonghae.userservice.error.ErrorCode;
import com.meonghae.userservice.error.exception.InvalidTokenException;
import com.meonghae.userservice.error.exception.JwtExpiredException;
import com.meonghae.userservice.error.exception.IllegalArgumentException;
import com.meonghae.userservice.error.exception.UnsupportedJwtException;
import com.meonghae.userservice.error.exception.SignatureException;
import com.meonghae.userservice.service.Jwt.CustomUserDetailService;
import com.meonghae.userservice.service.Jwt.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class JwtTokenProvider {

    private final RedisService redisService;
    private final CustomUserDetailService customUserDetailService;

    // 키
    @Value("${jwt.secret}")
    private String secretKey;

    // 액세스 토큰 유효시간 | 1h
    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenValidTime;
    // 리프레시 토큰 유효시간 | 7d
    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenValidTime;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct // 의존성 주입 후, 초기화를 수행
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Access Token 생성.
    public String createAccessToken(String email, UserRole userRole, String androidId) {
        return this.createToken(email, userRole, accessTokenValidTime, androidId);
    }

    // Refresh Token 생성.
    public String createRefreshToken(String email, UserRole userRole, String androidId) {
        return this.createToken(email, userRole, refreshTokenValidTime, androidId);
    }

    // Create token
    public String createToken(String email, UserRole userRole, long tokenValid, String androidId) {
        Claims claims = Jwts.claims().setSubject(email); // claims 생성 및 payload 설정
        List<String> roles = new ArrayList<>();
        roles.add(userRole.toString());
        claims.put("roles", roles); // 권한 설정, key/ value 쌍으로 저장
        claims.put("androidId", androidId);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Date date = new Date();

        return Jwts.builder()
                .setClaims(claims) // 발행 유저 정보 저장
                .setIssuedAt(date) // 발행 시간 저장
                .setExpiration(new Date(date.getTime() + tokenValid)) // 토큰 유효 시간 저장
                .signWith(key, SignatureAlgorithm.HS256) // 해싱 알고리즘 및 키 설정
                .compact(); // 생성
    }

    // JWT 토큰에서 인증 정보 조회
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(this.getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 AndroidId 정보 추출
    public String getAndroidIdFromToken(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build();

        return (String) jwtParser.parseClaimsJws(token).getBody().get("androidId");
    }

    // 토큰에서 회원 정보 추출
    public String getUserEmail(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build();

        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 AccessToken 값을 가져옵니다. "authorization" : "token"
    // Gateway 서비스에서 이미 Substring(7)을 했기 때문에 헤더에서 바로 가져온다.
    public String resolveAccessToken(HttpServletRequest request) {
        if(request.getHeader("Authorization") != null )
            return request.getHeader("Authorization");
        return null;
    }

    // Request의 Header에서 RefreshToken 값을 가져옵니다. "refreshToken" : "token"
    // Gateway 서비스에서 이미 Substring(7)을 했기 때문에 헤더에서 바로 가져온다.
    public String resolveRefreshToken(HttpServletRequest request) {
        if(request.getHeader("refreshToken") != null )
            return request.getHeader("refreshToken");
        return null;
    }

    // Expire Token
    public void expireToken(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Date expiration = claims.getExpiration();
        Date now = new Date();
        if (now.after(expiration)) {
            redisService.addTokenToBlacklist(token, expiration.getTime() - now.getTime());
        }
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
            throw new InvalidTokenException("4001", ErrorCode.INVALID_TOKEN_EXCEPTION);
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("4002", ErrorCode.JWT_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("4003", ErrorCode.UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("4004", ErrorCode.EMPTY_JWT_CLAIMS);
        } catch (SignatureException e) {
            throw new SignatureException("4005", ErrorCode.JWT_SIGNATURE_MISMATCH);
        }
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", "Bearer "+ accessToken);
    }

    // 리프레시 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("refreshToken", "Bearer "+ refreshToken);
    }
}
