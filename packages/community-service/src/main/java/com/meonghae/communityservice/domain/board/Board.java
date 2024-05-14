package com.meonghae.communityservice.domain.board;

import com.meonghae.communityservice.dto.board.BoardRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Board {
    private final Long id;
    private final String email;
    private final String title;
    private final String content;
    private final BoardType type;
    private int likes;
    private Boolean hasImage;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    @Builder
    public Board(Long id, String email, String title, String content, BoardType type, int likes, Boolean hasImage,
                 LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.content = content;
        this.type = type;
        this.likes = likes;
        this.hasImage = hasImage;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static Board create(BoardRequest request, BoardType type, String email) {
        return Board.builder()
                .email(email)
                .likes(0)
                .title(request.getTitle())
                .content(request.getContent())
                .type(type)
                .hasImage(false)
                .build();
    }

    public void incrementLikes() {
        this.likes += 1;
    }

    public void decrementLikes() {
        this.likes -= 1;
    }

    public Board updateBoard(String title, String content) {
        return Board.builder()
                .id(this.id)
                .email(this.email)
                .title(title)
                .content(content)
                .type(this.type)
                .likes(this.likes)
                .hasImage(this.hasImage)
                .createdDate(this.createdDate)
                .build();
    }

    public void toggleHasImage() {
        this.hasImage = !this.hasImage;
    }
}
