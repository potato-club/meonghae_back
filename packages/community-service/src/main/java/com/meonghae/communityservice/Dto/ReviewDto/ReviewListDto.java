package com.meonghae.communityservice.Dto.ReviewDto;

import com.meonghae.communityservice.Entity.Review.Review;
import com.meonghae.communityservice.Entity.Review.ReviewImage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewListDto {
    @ApiModelProperty("리뷰 id")
    private Long id;
    @ApiModelProperty("리뷰 작성자 닉네임")
    private String nickname;
    @ApiModelProperty("리뷰 제목")
    private String title;
    @ApiModelProperty("리뷰 내용")
    private String content;
    private List<ReviewImage> images;
    @ApiModelProperty("리뷰 별점")
    private int rating;

    public ReviewListDto(Review review, String nickname) {
        this.id = review.getId();
        this.nickname = nickname;
        this.title = review.getTitle();
        this.content = review.getContent();
        this.images = review.getImages();
        this.rating = review.getRating();
    }
}
