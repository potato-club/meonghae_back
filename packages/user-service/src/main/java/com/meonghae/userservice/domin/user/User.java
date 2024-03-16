package com.meonghae.userservice.domin.user;

import com.meonghae.userservice.domin.user.enums.UserRole;
import com.meonghae.userservice.dto.user.UserUpdate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class User {

    private final String uid;
    private final String email;
    private final int age;
    private final LocalDate birth;
    private final String nickname;
    private final UserRole userRole;
    private final boolean deleted;

    @Builder
    public User(String uid, String email, int age, LocalDate birth, String nickname,
                UserRole userRole, boolean deleted) {
        this.uid = uid;
        this.email = email;
        this.age = age;
        this.birth = birth;
        this.nickname = nickname;
        this.userRole = userRole;
        this.deleted = deleted;
    }

    public void update(UserUpdate userDto, LocalDate birth) {
        User.builder()
                .uid(uid)
                .email(email)
                .age(userDto.getAge())
                .birth(birth)
                .nickname(userDto.getNickname())
                .userRole(userRole)
                .deleted(deleted)
                .build();
    }

    public void delete(boolean deleted) {
        User.builder()
                .uid(uid)
                .email(email)
                .age(age)
                .birth(birth)
                .nickname(nickname)
                .userRole(userRole)
                .deleted(deleted)
                .build();
    }
}
