package com.meonghae.userservice.dto.file;

import com.meonghae.userservice.domin.file.enums.FileEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class S3Response {

    @ApiModelProperty(value = "파일 이름", example = "UUID - OriginName")
    private String fileName;

    @ApiModelProperty(value = "파일 URL")
    private String fileUrl;

    @ApiModelProperty(value = "엔티티 타입", example = "USER")
    private FileEnum entityType;

    @ApiModelProperty(value = "사용자 Email", example = "test@test.com")
    private String email;

    @Builder
    public S3Response(String fileName, String fileUrl, FileEnum entityType, String email) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.entityType = entityType;
        this.email = email;
    }
}
