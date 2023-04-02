package com.meonghae.communityservice.Entity.Board;

import com.meonghae.communityservice.Enum.BoardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String owner;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private BoardType type;
    private int likes;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<BoardImage> images;
    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<BoardComment> comments;

    public void setOwner(String userId) {
        this.owner = userId;
    }
}
