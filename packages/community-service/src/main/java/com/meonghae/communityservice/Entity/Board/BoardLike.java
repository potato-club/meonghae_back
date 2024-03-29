package com.meonghae.communityservice.Entity.Board;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class BoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @Column(nullable = false)
    private Boolean status;

    public BoardLike(String email, Board board) {
        this.email = email;
        this.board = board;
        this.status = true;
    }

    public void cancelLike(Board board) {
        board.setLikes(board.getLikes() - 1);
        this.status = !this.status;
    }

    public void addLike(Board board) {
        board.setLikes(board.getLikes() + 1);
        this.status = !this.status;
    }
}
