package com.meonghae.userservice.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UserUpdate {

    @ApiModelProperty(value = "닉네임")
    private final String nickname;

    @ApiModelProperty(value = "나이")
    private final int age;

    @ApiModelProperty(value = "생년월일")
    private final String birth;

    @ApiModelProperty(value = "프로필 사진")
    private final MultipartFile file;

    @Builder
    public UserUpdate(String nickname, int age, String birth,
                       MultipartFile file) {
        this.nickname = nickname;
        this.age = age;
        this.birth = birth;
        this.file = file;
    }
}
