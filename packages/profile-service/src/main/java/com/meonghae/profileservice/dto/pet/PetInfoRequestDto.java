package com.meonghae.profileservice.dto.pet;

import com.meonghae.profileservice.enumCustom.PetGender;
import com.meonghae.profileservice.enumCustom.PetType;

import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class PetInfoRequestDto {
  private PetType petType;
  private String petName;
  private PetGender petGender;

  private LocalDate petBirth;

  private String petSpecies;
}
