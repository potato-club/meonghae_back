package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Enum.BoardType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BoardRequestDto {
    @ApiModelProperty("게시글 제목")
    private String title;
    @ApiModelProperty("게시글 내용")
    private String content;

    public Board toEntity(BoardType type, String email) {
        return Board.builder()
                .email(email)
                .likes(0)
                .title(title)
                .content(content)
                .type(type)
                .hasImage(false)
                .build();
    }
}
