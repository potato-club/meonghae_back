package com.meonghae.userservice.dto.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ImageList {
    @ApiModelProperty(value = "파일 이름", example = "UUID - OriginName")
    private final String fileName;
    @ApiModelProperty(value = "파일 URL")
    private final String fileUrl;

    @Builder
    public ImageList(S3Response responseDto) {
        this.fileName = responseDto.getFileName();
        this.fileUrl = responseDto.getFileUrl();
    }
}
