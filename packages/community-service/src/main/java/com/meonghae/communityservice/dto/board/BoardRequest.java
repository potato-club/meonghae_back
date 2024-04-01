package com.meonghae.communityservice.dto.board;

import com.meonghae.communityservice.infra.board.board.BoardEntity;
import com.meonghae.communityservice.domain.board.BoardType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardRequest {
    @ApiModelProperty("게시글 제목")
    private String title;
    @ApiModelProperty("게시글 내용")
    private String content;
    @ApiModelProperty("이미지 파일")
    private List<MultipartFile> images;

    @Builder
    public BoardRequest(String title, String content, List<MultipartFile> images) {
        this.title = title;
        this.content = content;
        this.images = images;
    }

    public BoardEntity toEntity(BoardType type, String email) {
        return BoardEntity.builder()
                .email(email)
                .likes(0)
                .title(title)
                .content(content)
                .type(type)
                .hasImage(false)
                .build();
    }
}
