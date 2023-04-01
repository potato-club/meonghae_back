package com.meonghae.communityservice.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BoardType {
    SHOW(1, "멍자랑"), FUN(2, "웃긴멍"), MISSING(3, "실종신고");
    private final int key;
    private final String title;

    public static BoardType findWithKey(int key) {
        return Arrays.stream(values()).filter(state -> state.key == key).findAny().orElse(null);
    }
}
