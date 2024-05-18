package com.meonghae.communityservice.domain.board;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BoardComment {
    private final Long id;
    private final String comment;
    private final String email;
    private final Board board;
    private BoardComment parent;
    private final List<BoardComment> replies;
    private Boolean updated;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    @Builder
    public BoardComment(Long id, String comment, String email, Board board, BoardComment parent,
                        List<BoardComment> replies, Boolean updated,
                        LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.comment = comment;
        this.email = email;
        this.board = board;
        this.parent = parent;
        this.replies = replies != null ? replies : new ArrayList<>();
        this.updated = updated;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static BoardComment create(Board findBoard, String comment, String email) {
        return BoardComment.builder()
                .board(findBoard)
                .comment(comment)
                .updated(false)
                .email(email).build();
    }

    public void addReply(BoardComment reply) {
        this.replies.add(reply);
    }

    public boolean isParent() {
        return this.parent == null;
    }

    public void setParent(BoardComment parent) {
        this.parent = parent;
    }

    public BoardComment updateComment(String newComment) {
        return BoardComment.builder()
                .id(this.id)
                .updated(true)
                .comment(newComment)
                .email(this.email)
                .board(this.board)
                .createdDate(this.createdDate)
                .modifiedDate(this.modifiedDate)
                .parent(this.parent)
                .replies(this.replies)
                .build();
    }
}
