package com.meonghae.s3fileservice.dto;

import com.meonghae.s3fileservice.entity.File;
import com.meonghae.s3fileservice.enums.EntityType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileUpdateDto {

    @ApiModelProperty(value = "파일 이름")
    private String fileName;

    @ApiModelProperty(value = "파일 Url")
    private String fileUrl;

    @ApiModelProperty(value = "엔티티 종류")
    private EntityType entityType;

    @ApiModelProperty(value = "엔티티 Id 번호(User 엔티티 제외)")
    private Long entityId;

    @ApiModelProperty(value = "User 엔티티 email 값")
    private String email;

    @ApiModelProperty(value = "파일 삭제/교체 여부")
    private boolean deleted;

    public File toEntity() {
        return File.builder()
                .fileName(fileName)
                .fileUrl(fileUrl)
                .entityType(entityType)
                .typeId(entityId)
                .email(email)
                .build();
    }
}
