package com.meonghae.communityservice.dto.review;

import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.dto.s3.ImageList;
import com.meonghae.communityservice.dto.s3.S3Response;
import com.meonghae.communityservice.domain.review.RecommendStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewListDto {
    @ApiModelProperty("리뷰 id")
    private Long id;
    @ApiModelProperty("리뷰 작성자 닉네임")
    private String nickname;
    @ApiModelProperty("게시글 작성자 프로필 사진")
    private String profileUrl;
    @ApiModelProperty("리뷰 제목")
    private String title;
    @ApiModelProperty("리뷰 내용")
    private String content;
    @ApiModelProperty("리뷰 이미지 리스트")
    private List<ImageList> images;
    @ApiModelProperty("리뷰 별점")
    private int rating;
    @ApiModelProperty("리뷰 추천 수")
    private int likes;
    @ApiModelProperty("리뷰 비추천 수")
    private int dislikes;
    @ApiModelProperty("리뷰 추천 상태")
    private RecommendStatus recommendStatus;
    @ApiModelProperty("리뷰 작성자 여부")
    private boolean writer;
    @ApiModelProperty("리뷰 등록 날짜")
    private LocalDateTime date;

    public ReviewListDto(Review review, String nickname, String url, RecommendStatus status, boolean isWriter) {
        this.id = review.getId();
        this.nickname = nickname;
        this.profileUrl = url;
        this.title = review.getTitle();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.likes = review.getLikes();
        this.dislikes = review.getDislikes();
        this.recommendStatus = status;
        this.writer = isWriter;
        this.date = review.getCreatedDate();
    }

    public void setImages(List<S3Response> imageList) {
        this.images = imageList.stream().map(ImageList::new).collect(Collectors.toList());
    }
}
