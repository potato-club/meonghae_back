package com.meonghae.userservice.dto.file;

import com.meonghae.userservice.domin.file.enums.FileEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class S3Update {

    @ApiModelProperty(value = "파일 이름", example = "UUID - OriginName")
    private final String fileName;

    @ApiModelProperty(value = "파일 URL")
    private final String fileUrl;

    @ApiModelProperty(value = "저장할 타입", example = "USER")
    private final FileEnum entityType;

    @ApiModelProperty(value = "엔티티 ID", example = "1")
    private final Long entityId;

    @ApiModelProperty(value = "이메일", example = "test@test.com")
    private final String email;

    @ApiModelProperty(value = "삭제 여부", example = "true / false")
    private final boolean deleted;

    @Builder
    public S3Update(String fileName, String fileUrl, FileEnum entityType, Long entityId,
                    String email, boolean deleted) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.entityType = entityType;
        this.entityId = entityId;
        this.email = email;
        this.deleted = deleted;
    }
}
