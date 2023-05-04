package com.meonghae.communityservice.Dto.BoardDto;

import com.meonghae.communityservice.Entity.Board.Board;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardListDto {
    @ApiModelProperty("게시글 id")
    private Long id;
    @ApiModelProperty("게시글 작성자 닉네임")
    private String nickname;
    @ApiModelProperty("게시글 제목")
    private String title;
    @ApiModelProperty("게시글 내용")
    private String content;
    @ApiModelProperty("게시글 좋아요 개")
    private int likes;
    @ApiModelProperty("게시글 총 댓글 개수")
    private int commentSize;
    @ApiModelProperty("게시글 내 이미지 여부")
    private boolean hasImage;

    public BoardListDto(Board board, String nickname) {
        this.id = board.getId();
        this.nickname = nickname;
        this.title = board.getTitle();
        this.content = board.getContent();
        this.likes = board.getLikes();
        this.commentSize = board.getComments().isEmpty() ? 0 : board.getComments().size();
        this.hasImage = !board.getImages().isEmpty();
    }
}
