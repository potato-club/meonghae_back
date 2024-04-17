package com.meonghae.communityservice.domain.board;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardLike {
    private final Long id;
    private final String email;
    private final Board board;
    private Boolean status;

    @Builder
    public BoardLike(Long id, String email, Board board, Boolean status) {
        this.id = id;
        this.email = email;
        this.board = board;
        this.status = status;
    }

    public static BoardLike create(String email, Board board) {
        return BoardLike.builder()
                .email(email)
                .board(board)
                .status(true)
                .build();
    }

    public void toggleLike() {
        this.status = !this.status;
    }
}
