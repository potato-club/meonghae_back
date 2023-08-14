package com.meonghae.profileservice.dto.pet;

import com.meonghae.profileservice.dto.S3.S3ResponseDto;
import com.meonghae.profileservice.entity.Pet;
import io.swagger.annotations.ApiModelProperty;
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
  @ApiModelProperty(notes = "반려동물 id", example = "Long")
  private Long id;
  @ApiModelProperty(notes = "반려동물 이름", example = "시츄")
  private String petName;
  @ApiModelProperty(notes = "사진 정보")
  private S3ResponseDto s3ResponseDto;

  public PetInfoResponseDTO(Pet pet,S3ResponseDto s3ResponseDto) {
    this.id = pet.getId();
    this.petName = pet.getPetName();
    this.s3ResponseDto = s3ResponseDto;
  }
  public PetInfoResponseDTO(Pet pet) {
    this.id = pet.getId();
    this.petName = pet.getPetName();
  }
}
