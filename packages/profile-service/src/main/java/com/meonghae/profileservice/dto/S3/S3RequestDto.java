package com.meonghae.profileservice.dto.S3;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@Getter
public class S3RequestDto {
    private Long entityId;
    private String entityType;

    public S3RequestDto(Long id,String type){
        entityId = id;
        entityType = type;
    }
}
