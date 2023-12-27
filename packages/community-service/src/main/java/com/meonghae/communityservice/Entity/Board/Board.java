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
    @Column
    @Enumerated(value = EnumType.STRING)
    private BoardType type;
    private int likes;
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean hasImage;
    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<BoardComment> comments = new ArrayList<>();

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public void toggleHasImage() {
        this.hasImage = !this.hasImage;
    }
}
