package com.meonghae.communityservice.dto.s3;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageList {
    @ApiModelProperty("이미지 파일 이름")
    private String fileName;
    @ApiModelProperty("이미지 파일 url")
    private String fileUrl;

    public ImageList(S3Response responseDto) {
        this.fileName = responseDto.getFileName();
        this.fileUrl = responseDto.getFileUrl();
    }
}
