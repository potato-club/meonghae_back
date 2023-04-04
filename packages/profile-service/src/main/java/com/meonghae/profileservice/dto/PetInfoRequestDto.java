package com.meonghae.profileservice.dto;

import com.meonghae.profileservice.enumCustom.PetGender;
//import com.querydsl.core.annotations.QueryProjection;
import com.meonghae.profileservice.enumCustom.PetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class PetInfoRequestDto {
    private String userName;
    private PetType petType;
    private String petName;

    private PetGender petGender;

    private Date petBirth;

    private String petSpecies;

}
