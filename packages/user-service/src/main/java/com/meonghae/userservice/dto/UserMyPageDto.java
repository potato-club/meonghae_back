package com.meonghae.userservice.dto;

import com.meonghae.userservice.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class UserMyPageDto {

    @ApiModelProperty(value = "카카오 Email")
    private String email;

    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "나이")
    private int age;

    @ApiModelProperty(value = "생년월일", example = "20010101")
    private LocalDate birth;

    public UserMyPageDto(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.age = user.getAge();
        this.birth = user.getBirth();
    }
}
