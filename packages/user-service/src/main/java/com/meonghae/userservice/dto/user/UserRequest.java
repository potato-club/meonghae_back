package com.meonghae.userservice.dto.user;

import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.domin.user.enums.UserRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class UserRequest {

    @ApiModelProperty(value = "카카오 Email")
    private final String email;

    @ApiModelProperty(value = "닉네임")
    private final String nickname;

    @ApiModelProperty(value = "나이")
    private final int age;

    @ApiModelProperty(value = "생년월일")
    private final String birth;

    @ApiModelProperty(value = "프로필 사진")
    private final MultipartFile file;

    @Builder
    public UserRequest(String email, String nickname, int age, String birth,
                       MultipartFile file) {
        this.email = email;
        this.nickname = nickname;
        this.age = age;
        this.birth = birth;
        this.file = file;
    }

    public User toDomain() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return User.builder()
                .uid(String.valueOf(UUID.randomUUID()))
                .email(email)
                .nickname(nickname)
                .userRole(UserRole.USER)
                .age(age)
                .birth(LocalDate.parse(birth, formatter))
                .deleted(false)
                .build();
    }
}
