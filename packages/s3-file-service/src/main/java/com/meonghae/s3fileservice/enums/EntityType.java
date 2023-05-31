package com.meonghae.s3fileservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntityType {

    USER("ROLE_USER", "유저 엔티티"),
    BOARD("ROLE_BOARD", "게시글 엔티티"),
    REVIEW("ROLE_REVIEW", "리뷰 엔티티"),
    PET("ROLE_PET", "애완동물 엔티티"),
    ADMIN("ROLE_ADMIN", "관리자 엔티티");

    private final String key;
    private final String title;
}
