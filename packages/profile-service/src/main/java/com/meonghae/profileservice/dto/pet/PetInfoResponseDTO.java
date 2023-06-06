package com.meonghae.profileservice.dto.pet;

import com.meonghae.profileservice.dto.S3.S3ResponseDto;
import com.meonghae.profileservice.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetInfoResponseDTO {
  private Long id;
  private String petName;
  private S3ResponseDto s3ResponseDto;

  public PetInfoResponseDTO(Pet pet) {
    this.id = pet.getId();
    this.petName = pet.getPetName();
  }
  public PetInfoResponseDTO(Pet pet,S3ResponseDto s3ResponseDto) {
    this.id = pet.getId();
    this.petName = pet.getPetName();
    this.s3ResponseDto = s3ResponseDto;
  }

  public void setImage(S3ResponseDto s3ResponseDto) {
    this.s3ResponseDto = s3ResponseDto;
  }
}
