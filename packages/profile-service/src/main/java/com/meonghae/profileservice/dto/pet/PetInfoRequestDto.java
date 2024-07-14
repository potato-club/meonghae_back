package com.meonghae.profileservice.dto.pet;

import com.meonghae.profileservice.enumcustom.PetGender;
import java.time.LocalDate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PetInfoRequestDto {

  @ApiModelProperty(notes = "반려동물 이름", example = "뭉치", required = true)
  private String petName;
  @ApiModelProperty(notes = "반려동물 성별", example = "BOY or GIRL", required = true)
  private PetGender petGender;
  @ApiModelProperty(notes = "반려동물 생일", example = "2022-01-01", required = true)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate petBirth;
  @ApiModelProperty(notes = "반려동물 종", example = "시츄", required = true)
  private String petSpecies;
  @ApiModelProperty(notes = "입양 경로", example = "간택", required = true)
  private String meetRoute;
  @ApiModelProperty("이미지 파일")
  private MultipartFile image;

}
