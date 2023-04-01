package com.meonghae.communityservice.Entity.Board;

import com.meonghae.communityservice.Enum.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private BoardType type;
    private int likes;

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<BoardImage> images;
    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<BoardComment> comments;

}
