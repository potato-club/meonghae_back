package com.meonghae.profileservice.entity;

import com.meonghae.profileservice.dto.PetInfoRequestDto;
import com.meonghae.profileservice.enumCustom.PetGender;
import com.meonghae.profileservice.enumCustom.PetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String userName;
    @Column(nullable = false)
    private PetType petType;
    @Column(nullable = false)
    private String petName;
    @Column(nullable = false)
    private PetGender petGender;
    @Column(nullable = false)
    private Date petBirth;
    @Column(nullable = false)
    private String petSpecies;

}
