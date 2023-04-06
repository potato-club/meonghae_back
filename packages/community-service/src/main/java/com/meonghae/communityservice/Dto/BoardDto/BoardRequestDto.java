package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Enum.BoardType;
import lombok.Data;

@Data
public class BoardRequestDto {
    private String title;
    private String content;
    private String userId;

    public Board toEntity(BoardType type) {
        return Board.builder()
                .likes(0)
                .title(title)
                .content(content)
                .type(type)
                .build();
    }
}
