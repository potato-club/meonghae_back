package com.meonghae.communityservice.Entity.Review;

import com.meonghae.communityservice.Entity.BaseTimeEntity;
import com.meonghae.communityservice.Enum.ReviewCatalog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseTimeEntity {
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

    @Column(nullable = false)
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
}
