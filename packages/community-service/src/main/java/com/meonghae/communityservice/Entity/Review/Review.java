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
    private String userId;
    @OneToMany(mappedBy = "review", orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();
    @Column(name = "rating", nullable = false, columnDefinition = "int")
    private int rating;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewCatalog catalog;
}
