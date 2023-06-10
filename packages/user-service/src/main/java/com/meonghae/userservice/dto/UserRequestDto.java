package com.meonghae.userservice.dto;

import com.meonghae.userservice.entity.User;
import com.meonghae.userservice.enums.UserRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @ApiModelProperty(value = "카카오 Email")
    private String email;

    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "나이")
    private int age;

    @ApiModelProperty(value = "생년월일")
    private String birth;

    public User toEntity() {
        User user = User.builder()
                .uid(String.valueOf(UUID.randomUUID()))
                .email(email)
                .nickname(nickname)
                .userRole(UserRole.USER)
                .age(age)
                .birth(LocalDateTime.parse(birth, formatter))
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .deleted(false)
                .build();

        return user;
    }
}
