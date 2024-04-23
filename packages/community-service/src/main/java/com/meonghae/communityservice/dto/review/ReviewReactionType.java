package com.meonghae.communityservice.dto.review;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewReactionType {
    @ApiModelProperty(value = "추천 여부", example = "추천 - true / 비추 - false")
    private Boolean isLike;

    @Builder
    public ReviewReactionType(Boolean isLike) {
        this.isLike = isLike;
    }
}
