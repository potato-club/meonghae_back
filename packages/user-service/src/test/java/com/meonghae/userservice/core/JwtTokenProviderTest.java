package com.meonghae.userservice.core;

import com.meonghae.userservice.core.jwt.JwtTokenProvider;
import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.domin.user.enums.UserRole;
import com.meonghae.userservice.infra.repository.RedisService;
import com.meonghae.userservice.mock.FakeRedisService;
import com.meonghae.userservice.service.port.UserRepository;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {"spring.config.location = classpath:application-test-user.yml"})
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

    @BeforeEach
    void init() {
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

        Map<String, Object> map = new HashMap<>();
        FakeRedisService fakeRedisService = new FakeRedisService(map);

        // when
        fakeRedisService.addTokenToBlacklist(accessToken);

        // then
        assertThatThrownBy(() -> {
            fakeRedisService.isTokenInBlacklist(accessToken);
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
