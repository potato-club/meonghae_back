package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.BoardImage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardDetailDto {
    @ApiModelProperty("게시글 id")
    private Long id;
    @ApiModelProperty("게시글 작성자 닉네임")
    private String nickname;
    @ApiModelProperty("게시글 제목")
    private String title;
    @ApiModelProperty("게시글 내용")
    private String content;
    private List<BoardImage> images;
    @ApiModelProperty("게시글 댓글 총 개수")
    private int commentSize;
    @ApiModelProperty("게시글 좋아요 개수")
    private int likes;

    public BoardDetailDto(Board board, String nickname) {
        this.id = board.getId();
        this.nickname = nickname;
        this.title = board.getTitle();
        this.content = board.getContent();
        this.commentSize = board.getComments().isEmpty() ? 0 : board.getComments().size();
        this.images = board.getImages();
        this.likes = board.getLikes();
    }
}
