package com.meonghae.profileservice.dto.S3;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3UpdateDto {
    @ApiModelProperty(value = "파일 이름")
    private String fileName;

    @ApiModelProperty(value = "파일 Url")
    private String fileUrl;

    @ApiModelProperty(value = "엔티티 종류")
    private String entityType;

    @ApiModelProperty(value = "엔티티 Id 번호(User 엔티티 제외)")
    private Long entityId;

    @ApiModelProperty(value = "User 엔티티 email 값")
    private String email;

    @ApiModelProperty(value = "파일 삭제/교체 여부")
    private boolean deleted;

    public S3UpdateDto (S3ResponseDto s3ResponseDto){
        fileName = s3ResponseDto.getFileName();
        fileUrl = s3ResponseDto.getFileUrl();
        entityType = s3ResponseDto.getEntityType();
        entityId = s3ResponseDto.getTypeId();
        deleted = true;
    }
}
