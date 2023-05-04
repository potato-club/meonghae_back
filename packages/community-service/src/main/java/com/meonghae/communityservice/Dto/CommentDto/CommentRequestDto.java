package com.meonghae.communityservice.Dto.CommentDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {
    @ApiModelProperty("댓글 작성자 닉네임")
    private String nickname;
    @ApiModelProperty("댓글 내용")
    private String comment;
}
