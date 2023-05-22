package com.meonghae.profileservice.dto.pet;

import com.meonghae.profileservice.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetInfoResponseDTO {
  private Long id;
  private String petName;

  public PetInfoResponseDTO(Pet pet) {
    this.id = pet.getId();
    this.petName = pet.getPetName();
  }
}
