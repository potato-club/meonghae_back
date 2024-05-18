package com.meonghae.userservice.infra.entity;

import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.domin.user.enums.UserRole;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "users")
public class UserEntity extends BaseTimeEntity {

    @Id
    private String uid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private int age;

    @Column
    private LocalDate birth;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;  // 유저의 직업을 나타내는 Enum (ex. USER, ARTIST)

    @Column
    private boolean deleted;

    public static UserEntity from(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.uid = user.getUid();
        userEntity.email = user.getEmail();
        userEntity.age = user.getAge();
        userEntity.birth = user.getBirth();
        userEntity.nickname = user.getNickname();
        userEntity.userRole = user.getUserRole();
        userEntity.deleted = user.isDeleted();

        return userEntity;
    }

    public User toModel() {
        return User.builder()
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
