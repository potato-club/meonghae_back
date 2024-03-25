package com.meonghae.userservice.service;

import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.domin.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;

public class UserServiceTest {

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


    }
}
