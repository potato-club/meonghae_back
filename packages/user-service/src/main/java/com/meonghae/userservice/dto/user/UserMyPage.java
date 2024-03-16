package com.meonghae.userservice.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserMyPage {

    @ApiModelProperty(value = "카카오 Email")
    private final String email;

    @ApiModelProperty(value = "닉네임")
    private final String nickname;

    @ApiModelProperty(value = "나이")
    private final int age;

    @ApiModelProperty(value = "생년월일")
    private final LocalDate birth;

    @ApiModelProperty(value = "프로필 파일 이름")
    private final String fileName;

    @ApiModelProperty(value = "프로필 파일 URL")
    private final String fileUrl;

    @Builder
    public UserMyPage(String email, String nickname, int age, LocalDate birth,
                      String fileName, String fileUrl) {
        this.email = email;
        this.nickname = nickname;
        this.age = age;
        this.birth = birth;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
