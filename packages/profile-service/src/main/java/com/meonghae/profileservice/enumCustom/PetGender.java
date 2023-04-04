package com.meonghae.profileservice.enumCustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetGender {
    Boy("male","남"),
    Girl("female","여");
    private final String key;
    private final String title;


}
