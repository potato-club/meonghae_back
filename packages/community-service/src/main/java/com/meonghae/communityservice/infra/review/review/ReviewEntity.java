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

    public static ReviewEntity fromModel(Review review) {
        return ReviewEntity.builder()
                .id(review.getId())
                .catalog(review.getCatalog())
                .content(review.getContent())
                .title(review.getTitle())
                .email(review.getEmail())
                .rating(review.getRating())
                .hasImage(review.getHasImage())
                .likes(review.getLikes())
                .dislikes(review.getDislikes())
                .build();
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
                .createdDate(this.getCreatedDate())
                .modifiedDate(this.getModifiedDate())
                .build();
    }

    public void updateReaction(Review review) {
        this.likes = review.getLikes();
        this.dislikes = review.getDislikes();
    }

    public void hasImage() {
        this.hasImage = true;
    }
}
