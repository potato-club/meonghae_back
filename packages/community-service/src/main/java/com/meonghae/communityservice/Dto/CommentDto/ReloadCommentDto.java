package com.meonghae.communityservice.Dto.CommentDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReloadCommentDto {
    private Long id;
    private Boolean parent;

    public ReloadCommentDto(Long id, Boolean isParent) {
        this.id = id;
        this.parent = isParent;
    }
}
