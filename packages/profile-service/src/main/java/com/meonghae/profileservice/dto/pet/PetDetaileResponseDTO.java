package com.meonghae.profileservice.dto.pet;

import com.meonghae.profileservice.dto.S3.S3ResponseDto;
import com.meonghae.profileservice.entity.Pet;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetDetaileResponseDTO {
  @ApiModelProperty(notes = "반려동물 id", example = "Long")
  private Long id;
  @ApiModelProperty(notes = "반려동물 이름", example = "시츄")
  private String petName;
  @ApiModelProperty(notes = "반려동물 성별", example = "BOY or GIRL")
  private String petGender;
  @ApiModelProperty(notes = "반려동물 생일", example = "2018-01-01")
  private String petBirth;
  @ApiModelProperty(notes = "반려동물 종", example = "문자열")
  private String petSpecies;
  @ApiModelProperty(notes = "입양 경로", example = "간택")
  private String meetRoute;
  @ApiModelProperty(notes = "사진 정보")
  private S3ResponseDto s3ResponseDto;

  public PetDetaileResponseDTO(Pet pet) {
    this.id = pet.getId();
    this.petName = pet.getPetName();
    this.petGender = pet.getPetGender().getKey();
    this.petBirth = pet.getPetBirth().toString();
    this.petSpecies = pet.getPetSpecies();
    this.meetRoute = pet.getMeetRoute();
  }

  public PetDetaileResponseDTO(Pet pet, S3ResponseDto image) {
    this.id = pet.getId();
    this.petName = pet.getPetName();
    this.petGender = pet.getPetGender().getKey();
    this.petBirth = pet.getPetBirth().toString();
    this.petSpecies = pet.getPetSpecies();
    this.meetRoute = pet.getMeetRoute();
    this.s3ResponseDto = image;
  }


  public void setImage(S3ResponseDto s3ResponseDto) {
    this.s3ResponseDto = s3ResponseDto;
  }
}
