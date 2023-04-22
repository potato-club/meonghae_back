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
    @ApiModelProperty("게시글 작성자 닉네임")
    private String nickname;

    public Board toEntity(BoardType type, String uid) {
        return Board.builder()
                .userId(uid)
                .likes(0)
                .title(title)
                .content(content)
                .type(type)
                .build();
    }
}
