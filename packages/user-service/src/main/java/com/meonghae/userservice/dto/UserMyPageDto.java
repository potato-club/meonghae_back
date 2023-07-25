package com.meonghae.userservice.dto;

import com.meonghae.userservice.dto.S3Dto.S3ResponseDto;
import com.meonghae.userservice.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserMyPageDto {

    @ApiModelProperty(value = "카카오 Email")
    private String email;

    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "나이")
    private int age;

    @ApiModelProperty(value = "생년월일", example = "20010101")
    private LocalDate birth;

    @ApiModelProperty(value = "프로필 파일 이름")
    private String fileName;

    @ApiModelProperty(value = "프로필 파일 URL")
    private String fileUrl;
}
