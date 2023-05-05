package com.meonghae.communityservice.Entity.Board;

import com.meonghae.communityservice.Entity.BaseTimeEntity;
import com.meonghae.communityservice.Enum.BoardType;
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
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private BoardType type;
    private int likes;

    // 추후 수정 필요 -> S3 업로드 MSA 로 구현 -> name 만 가져와서 String 타입으로 가질듯?
    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<BoardImage> images = new ArrayList<>();
    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<BoardComment> comments = new ArrayList<>();

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
