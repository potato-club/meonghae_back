package com.meonghae.profileservice.enumCustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetGender {
  BOY("male", "남"),
  GIRL("female", "여");
  private final String key;
  private final String title;
}
