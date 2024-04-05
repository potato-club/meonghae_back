package com.meonghae.userservice.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.domin.user.enums.UserRole;
import com.meonghae.userservice.dto.user.UserRequest;
import com.meonghae.userservice.dto.user.UserUpdate;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class UserTest {

    @Test
    void 회원_가입을_진행_할_수_있다() {
        // given
        UserRequest userRequest = UserRequest.builder()
                .email("test@test.com")
                .nickname("TEST_BackEnd")
                .age(25)
                .birth("20000101")
                .build();

        // when
        User user = userRequest.toDomain();

        // then
        assertThat(user.getUid()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getNickname()).isEqualTo("TEST_BackEnd");
        assertThat(user.getAge()).isEqualTo(25);
        assertThat(user.getBirth()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
        assertThat(user.isDeleted()).isEqualTo(false);
    }

    @Test
    void Update_객체로_데이터를_업데이트_할_수_있다() {
        // given
        User user = User.builder()
                .uid("1e420e42-93d7-47c2-90e2-61f939933350")
                .email("test@test.com")
                .age(25)
                .birth(LocalDate.of(2000, 1, 1))
                .nickname("TEST_BackEnd")
                .userRole(UserRole.USER)
                .deleted(false)
                .build();

        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("TEST_Admin")
                .age(24)
                .birth("20010101")
                .build();

        // when
        user.update(userUpdate, LocalDate.of(2001, 1, 1));

        // then
        assertThat(user.getUid()).isEqualTo("1e420e42-93d7-47c2-90e2-61f939933350");
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getNickname()).isEqualTo("TEST_Admin");
        assertThat(user.getAge()).isEqualTo(24);
        assertThat(user.getBirth()).isEqualTo(LocalDate.of(2001, 1, 1));
        assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
        assertThat(user.isDeleted()).isEqualTo(false);
    }

    @Test
    void 회원_탈퇴를_진행_할_수_있다() {
        // given
        User user = User.builder()
                .uid("1e420e42-93d7-47c2-90e2-61f939933350")
                .email("test@test.com")
                .age(25)
                .birth(LocalDate.of(2000, 1, 1))
                .nickname("TEST_BackEnd")
                .userRole(UserRole.USER)
                .deleted(false)
                .build();

        // when
        user.delete(true);

        // then
        assertThat(user.getUid()).isEqualTo("1e420e42-93d7-47c2-90e2-61f939933350");
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getNickname()).isEqualTo("TEST_BackEnd");
        assertThat(user.getAge()).isEqualTo(25);
        assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
        assertThat(user.isDeleted()).isEqualTo(true);
    }
}
