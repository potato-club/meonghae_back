package com.meonghae.communityservice.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ReviewCatalog {
    COLLAR(1, "넥카라"), LEASH(2, "목줄"), TOY(3, "장난감"), CUSHION(4, "방석"),
    BATH(5, "목욕용품"), FOOD(6, "사료"), GUM(7, "개껌"), MUZZLE(8, "입마개"),
    STROLLER(9, "유모차"), PAD(10, "배변패드"), SNACK(11, "간식"),
    BODY(12, "바디용품");
    private final int key;
    private final String name;

    public static ReviewCatalog findWithKey(int key) {
        return Arrays.stream(values()).filter(state -> state.key == key).findAny().orElse(null);
    }
}