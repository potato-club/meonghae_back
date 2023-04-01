package com.meonghae.communityservice.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {
    SHOW(1, "멍자랑"), FUN(2, "웃긴멍"), MISSING(3, "실종신고");
    private final int key;
    private final String title;
}
