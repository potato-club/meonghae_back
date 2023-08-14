package com.meonghae.userservice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UserCancelDto {

    @ApiModelProperty(value = "회원 탈퇴한 Email")
    private String email;

    @ApiModelProperty(value = "철회 동의 여부")
    private boolean agreement;
}
