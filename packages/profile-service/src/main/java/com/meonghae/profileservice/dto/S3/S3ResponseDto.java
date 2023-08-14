package com.meonghae.profileservice.dto.S3;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3ResponseDto {

    private String fileName;
    private String fileUrl;
    private String entityType;
    private String email;



}
