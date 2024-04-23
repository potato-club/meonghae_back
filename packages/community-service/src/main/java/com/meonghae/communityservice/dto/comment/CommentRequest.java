package com.meonghae.communityservice.dto.comment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequest {
    @ApiModelProperty("댓글 내용")
    private String comment;

    @Builder
    public CommentRequest(String comment) {
        this.comment = comment;
    }
}
