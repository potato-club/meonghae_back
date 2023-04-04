package com.meonghae.profileservice.dto;

import com.meonghae.profileservice.entity.PetEntity;
import com.meonghae.profileservice.enumCustom.PetGender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetInfoResponseDTO {
    private Long id;
    private String petName;

    public PetInfoResponseDTO (PetEntity petEntity){
        this.id =petEntity.getId();
        this.petName = petEntity.getPetName();
    }
}
