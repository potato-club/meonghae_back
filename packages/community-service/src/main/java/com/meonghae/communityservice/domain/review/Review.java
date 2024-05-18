package com.meonghae.communityservice.domain.review;

import com.meonghae.communityservice.dto.review.ReviewRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Review {
    private final Long id;

    private final String title;

    private final String content;

    private final String email;

    private Boolean hasImage;

    private final int rating;

    private final ReviewCatalog catalog;

    private int likes;

    private int dislikes;

    private final LocalDateTime createdDate;

    private final LocalDateTime modifiedDate;

    @Builder
    public Review(Long id, String title, String content, String email, Boolean hasImage,
                  int rating, ReviewCatalog catalog, int likes, int dislikes,
                  LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.email = email;
        this.hasImage = hasImage;
        this.rating = rating;
        this.catalog = catalog;
        this.likes = likes;
        this.dislikes = dislikes;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static Review create(ReviewRequest requestDto, ReviewCatalog catalog, String email) {
        return Review.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .email(email)
                .rating(requestDto.getRating())
                .catalog(catalog)
                .likes(0)
                .dislikes(0)
                .hasImage(false).build();
    }

    public void addLikes() {
        this.likes++;
    }

    public void addDislikes() {
        this.dislikes++;
    }

    public void cancelLikes() {
        this.likes--;
    }

    public void cancelDislikes() {
        this.dislikes--;
    }

    public void setHasImage() {
        this.hasImage = true;
    }
}
