package com.meonghae.communityservice.domain.review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecommendStatus {
    TRUE(1, "추천"), FALSE(2, "비추천"), NONE(3, "없음");
    private final int key;
    private final String value;
}
