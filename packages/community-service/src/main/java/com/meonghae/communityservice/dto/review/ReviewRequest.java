package com.meonghae.communityservice.dto.review;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.List;

@Getter
public class ReviewRequest {

    @ApiModelProperty("리뷰 제목")
    @NotBlank(message = "제목은 반드시 필요합니다.")
    private String title;

    @ApiModelProperty("리뷰 내용")
    @NotBlank(message = "내용은 반드시 필요합니다.")
    @Size(min = 5, message = "내용은 5자 이상 작성해야 합니다.")
    private String content;

    @ApiModelProperty("리뷰 별점 (0~10)")
    @NotNull(message = "별점은 반드시 필요합니다.")
    @DecimalMin(value = "0")
    @DecimalMax(value = "10")
    private int rating;

    @ApiModelProperty("리뷰 이미지")
    private List<MultipartFile> images;

    @Builder
    public ReviewRequest(String title, String content, int rating, List<MultipartFile> images) {
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.images = images;
    }
}
