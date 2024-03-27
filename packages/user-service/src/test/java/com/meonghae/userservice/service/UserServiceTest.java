package com.meonghae.userservice.service;

import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.domin.user.enums.UserRole;
import com.meonghae.userservice.mock.*;
import com.meonghae.userservice.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserServiceTest {

    private UserServiceImpl userService;

    @BeforeEach
    void init() {
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        Map<String, Object> map = new HashMap<>();

        this.userService = UserServiceImpl.builder()
                .userRepository(new FakeUserRepository())
                .fcmTokenRepository(new FakeFCMTokenRepository())
                .redisService(new FakeRedisService(map))
                .jwtTokenProvider(new FakeJwtTokenProvider())
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
}
