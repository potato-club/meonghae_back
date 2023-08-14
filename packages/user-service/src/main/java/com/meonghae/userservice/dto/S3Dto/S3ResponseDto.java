package com.meonghae.userservice.dto.S3Dto;

import com.meonghae.userservice.enums.FileEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3ResponseDto {

    @ApiModelProperty(value = "파일 이름", example = "UUID - OriginName")
    private String fileName;

    @ApiModelProperty(value = "파일 URL")
    private String fileUrl;

    @ApiModelProperty(value = "엔티티 타입", example = "USER")
    private FileEnum entityType;

    @ApiModelProperty(value = "사용자 Email", example = "test@test.com")
    private String email;
}
