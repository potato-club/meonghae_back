package com.meonghae.profileservice.enumCustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetType {
  Dog("dog", "강아지"),
  Cat("cat", "고양이");

  private final String key;
  private final String title;
}
