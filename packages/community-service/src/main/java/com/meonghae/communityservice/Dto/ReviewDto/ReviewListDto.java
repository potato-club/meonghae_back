package com.meonghae.communityservice.Dto.ReviewDto;

import com.meonghae.communityservice.Dto.S3Dto.ImageListDto;
import com.meonghae.communityservice.Dto.S3Dto.S3ResponseDto;
import com.meonghae.communityservice.Entity.Review.Review;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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
    @ApiModelProperty("리뷰 이미지 리스트")
    private List<ImageListDto> images;
    @ApiModelProperty("리뷰 별점")
    private int rating;
    @ApiModelProperty("리뷰 추천 수")
    private int likes;
    @ApiModelProperty("리뷰 비추천 수")
    private int dislikes;

    public ReviewListDto(Review review, String nickname) {
        this.id = review.getId();
        this.nickname = nickname;
        this.title = review.getTitle();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.likes = review.getLikes();
        this.dislikes = review.getDislikes();
    }

    public void setImages(List<S3ResponseDto> imageList) {
        this.images = imageList.stream().map(ImageListDto::new).collect(Collectors.toList());
    }
}
