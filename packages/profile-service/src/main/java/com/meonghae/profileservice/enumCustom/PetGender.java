package com.meonghae.profileservice.enumCustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetGender {
  BOY("BOY", "남"),
  GIRL("GIRL", "여");
  private final String key;
  private final String title;
}
