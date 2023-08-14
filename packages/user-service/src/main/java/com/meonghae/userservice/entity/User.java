package com.meonghae.userservice.entity;

import com.meonghae.userservice.dto.UserUpdateDto;
import com.meonghae.userservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "users")
public class User extends BaseTimeEntity {

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

    public void update(UserUpdateDto userDto, LocalDate birth) {
        this.nickname = userDto.getNickname();
        this.age = userDto.getAge();
        this.birth = birth;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
