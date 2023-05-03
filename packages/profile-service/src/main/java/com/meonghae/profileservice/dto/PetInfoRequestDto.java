package com.meonghae.profileservice.dto;

import com.meonghae.profileservice.enumCustom.PetGender;
import com.meonghae.profileservice.enumCustom.PetType;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PetInfoRequestDto {
  private PetType petType;
  private String petName;

  private PetGender petGender;

  private Date petBirth;

  private String petSpecies;
}
