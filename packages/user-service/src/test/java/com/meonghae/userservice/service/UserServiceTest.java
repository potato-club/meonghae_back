package com.meonghae.userservice.service;

import com.meonghae.userservice.core.jwt.JwtTokenProviderImpl;
import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.domin.user.enums.UserRole;
import com.meonghae.userservice.dto.user.UserResponse;
import com.meonghae.userservice.mock.*;
import com.meonghae.userservice.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class UserServiceTest {

    private UserServiceImpl userService;

    @BeforeEach
    void init() {
        Map<String, Object> map = new HashMap<>();

        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        FakeRedisService fakeRedisService = new FakeRedisService(map);

        JwtTokenProviderImpl jwtTokenProvider = JwtTokenProviderImpl.builder()
                .userRepository(fakeUserRepository)
                .redisService(fakeRedisService)
                .secretKey("aaaaaaaaaaa-aaaaaaaaaaaaaaaaaaaaa-aaaaaaaaaaaaaaaa-aaaaaaaaaaaaaa")
                .accessTokenValidTime(180000L)
                .refreshTokenValidTime(180000L)
                .build();

        this.userService = UserServiceImpl.builder()
                .userRepository(fakeUserRepository)
                .fcmTokenRepository(new FakeFCMTokenRepository())
                .redisService(fakeRedisService)
                .jwtTokenProvider(jwtTokenProvider)
                .s3Service(new FakeS3ServiceClient())
                .petServiceClient(new FakePetServiceClient())
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

        fakeUserRepository.save(user);
    }

    @Test
    void login_을_성공적으로_마치면_토큰과_200_OK_를_반환한다() {
        // given
        String email = "test@test.com";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        UserResponse userResponse = userService.login(email, request, response);

        // then
        assertThat(response.getHeader("Authorization")).isNotNull();    // error
        assertThat(response.getHeader("RefreshToken")).isNotNull();
        assertThat(userResponse.getResponseCode()).isEqualTo("200_OK");
    }
}
