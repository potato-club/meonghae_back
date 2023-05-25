package com.meonghae.communityservice.Entity.Review;

import com.meonghae.communityservice.Entity.BaseTimeEntity;
import com.meonghae.communityservice.Enum.ReviewCatalog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    // 추후 수정 필요 -> S3 업로드 MSA 로 구현 -> name 만 가져와서 String 타입으로 가질듯?
    @OneToMany(mappedBy = "review", orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();
    @Column(name = "rating", nullable = false, columnDefinition = "int")
    private int rating;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewCatalog catalog;

    @Column(nullable = false)
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
}
