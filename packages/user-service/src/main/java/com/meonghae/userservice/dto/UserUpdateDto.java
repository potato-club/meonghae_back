package com.meonghae.userservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
public class UserUpdateDto {

    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "나이")
    private int age;

    @ApiModelProperty(value = "생년월일")
    private String birth;

    @ApiModelProperty(value = "프로필 사진")
    private MultipartFile file;
}
