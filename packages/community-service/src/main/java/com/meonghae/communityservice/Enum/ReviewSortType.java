package com.meonghae.communityservice.Enum;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum ReviewSortType {
    RECOMMEND, RATING_ASC, RATING_DESC, LATEST;

    public static ReviewSortType findType(String sort) {
        return Arrays.stream(values()).filter(type -> Objects.equals(type.toString(), sort)).findAny().orElse(null);
    }
}
