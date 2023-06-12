package com.meonghae.communityservice.Dto.ReviewDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReviewReactionTypeDto {
    @ApiModelProperty(value = "추천 여부", example = "추천 - true / 비추 - false")
    private Boolean isLike;
}
