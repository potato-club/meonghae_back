package com.meonghae.userservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileEnum {

    USER("ROLE_USER", "유저 엔티티");

    private final String key;
    private final String title;
}
