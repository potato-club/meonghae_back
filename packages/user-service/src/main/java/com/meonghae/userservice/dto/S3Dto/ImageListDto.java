package com.meonghae.userservice.dto.S3Dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageListDto {
    @ApiModelProperty("이미지 파일 이름")
    private String fileName;
    @ApiModelProperty("이미지 파일 url")
    private String fileUrl;

    public ImageListDto(S3ResponseDto responseDto) {
        this.fileName = responseDto.getFileName();
        this.fileUrl = responseDto.getFileUrl();
    }
}
