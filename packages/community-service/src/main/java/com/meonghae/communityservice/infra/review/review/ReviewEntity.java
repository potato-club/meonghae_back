package com.meonghae.communityservice.infra.review.review;

import com.meonghae.communityservice.domain.review.Review;
import com.meonghae.communityservice.infra.BaseTimeEntity;
import com.meonghae.communityservice.domain.review.ReviewCatalog;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "review")
public class ReviewEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean hasImage;

    @Column(name = "rating", nullable = false, columnDefinition = "int")
    private int rating;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewCatalog catalog;

    @Column(name = "likes", nullable = false, columnDefinition = "int")
    private int likes;

    @Column(nullable = false, columnDefinition = "int")
    private int dislikes;

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

    public static ReviewEntity fromModel(Review review) {
        ReviewEntity entity = new ReviewEntity();
        entity.id = review.getId();
        entity.catalog = review.getCatalog();
        entity.content = review.getContent();
        entity.title = review.getTitle();
        entity.email = review.getEmail();
        entity.rating = review.getRating();
        entity.hasImage = review.getHasImage();
        entity.likes = review.getLikes();
        entity.dislikes = review.getDislikes();
        return entity;
    }

    public Review toModel() {
        return Review.builder()
                .id(this.id)
                .email(this.email)
                .rating(this.rating)
                .title(this.title)
                .content(this.content)
                .hasImage(this.hasImage)
                .catalog(this.catalog)
                .likes(this.likes)
                .dislikes(this.dislikes)
                .build();
    }
}
