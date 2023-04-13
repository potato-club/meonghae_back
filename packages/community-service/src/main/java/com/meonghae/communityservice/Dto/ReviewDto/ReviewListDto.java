package com.meonghae.communityservice.Dto.ReviewDto;

import com.meonghae.communityservice.Entity.Review.Review;
import com.meonghae.communityservice.Entity.Review.ReviewImage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewListDto {
    private Long id;
    private String userId;
    private String title;
    private String content;
    private List<ReviewImage> images;
    private int rating;

    public ReviewListDto(Review review) {
        this.id = review.getId();
        this.userId = review.getUserId();
        this.title = review.getTitle();
        this.content = review.getContent();
        this.images = review.getImages();
        this.rating = review.getRating();
    }
}
