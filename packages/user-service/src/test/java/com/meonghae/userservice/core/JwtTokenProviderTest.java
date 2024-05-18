package com.meonghae.userservice.core;

import com.meonghae.userservice.core.jwt.JwtTokenProviderImpl;
import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.domin.user.enums.UserRole;
import com.meonghae.userservice.mock.FakeRedisService;
import com.meonghae.userservice.mock.FakeUserRepository;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JwtTokenProviderTest {

    private JwtTokenProviderImpl jwtTokenProvider;
    private FakeRedisService redisService;

    @BeforeEach
    void init() {

        Map<String, Object> map = new HashMap<>();
        FakeUserRepository userRepository = new FakeUserRepository();

        this.redisService = new FakeRedisService(map);
        this.jwtTokenProvider = JwtTokenProviderImpl.builder()
                .userRepository(userRepository)
                .redisService(this.redisService)
                .secretKey("aaaaaaaaaaa-aaaaaaaaaaaaaaaaaaaaa-aaaaaaaaaaaaaaaa-aaaaaaaaaaaaaa")
                .accessTokenValidTime(180000L)
                .refreshTokenValidTime(180000L)
                .build();

        User user = User.builder()
                .uid("123-456-789")
                .email("test@test.com")
                .age(25)
                .birth(LocalDate.of(2000, 1, 1))
                .nickname("Test-User")
                .userRole(UserRole.USER)
                .deleted(false)
                .build();

        userRepository.save(user);
    }

    @Test
    void AccessToken_을_발급한다() {
        // given
        String email = "test@test.com";
        UserRole userRole = UserRole.USER;
        String androidId = "hi-test-1234";

        // when
        String accessToken = jwtTokenProvider.createAccessToken(email, userRole, androidId);

        // then
        assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue();
    }

    @Test
    void RefreshToken_을_발급한다() {
        // given
        String email = "test@test.com";
        UserRole userRole = UserRole.USER;
        String androidId = "hi-test-1234";

        // when
        String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole, androidId);

        // then
        assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
    }

    @Test
    void Token_을_블랙리스트에_등록한다() {
        // given
        String email = "test@test.com";
        UserRole userRole = UserRole.USER;
        String androidId = "hi-test-1234";

        String accessToken = jwtTokenProvider.createAccessToken(email, userRole, androidId);

        // when
        redisService.addTokenToBlacklist(accessToken, 180000L);   // 1800000L은 더미 값

        // then
        assertThatThrownBy(() -> {
            redisService.isTokenInBlacklist(accessToken);
        }).isInstanceOf(MalformedJwtException.class)
                .hasMessage("Invalid JWT token");
    }

    @Test
    void RefreshToken_으로_AccessToken_을_재발급한다() {
        // given
        String email = "test@test.com";
        UserRole userRole = UserRole.USER;
        String androidId = "hi-test-1234";

        String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole, androidId);

        redisService.setValues(refreshToken, email, androidId);
        redisService.setValues(email, androidId, null, refreshToken);

        // when
        String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken, androidId);

        // then
        assertThat(jwtTokenProvider.validateToken(newAccessToken)).isTrue();
    }

    @Test
    void Token_에서_Email_을_얻을_수_있다() {
        // given
        String email = "test@test.com";
        UserRole userRole = UserRole.USER;
        String androidId = "hi-test-1234";

        String accessToken = jwtTokenProvider.createAccessToken(email, userRole, androidId);

        // when
        String userEmail = jwtTokenProvider.getUserEmail(accessToken);

        // then
        assertThat(userEmail).isEqualTo(email);
    }
}
