package com.meonghae.communityservice.dto.board;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class BoardUpdate {
    @ApiModelProperty("게시글 제목")
    private final String title;
    @ApiModelProperty("게시글 내용")
    private final String content;
    @ApiModelProperty("이미지 파일")
    private final List<MultipartFile> images;

    @Builder
    public BoardUpdate(String title, String content, List<MultipartFile> images) {
        this.title = title;
        this.content = content;
        this.images = images;
    }
}
