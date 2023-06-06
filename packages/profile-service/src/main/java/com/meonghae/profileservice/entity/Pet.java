package com.meonghae.profileservice.entity;

import com.meonghae.profileservice.dto.pet.PetInfoRequestDto;
import com.meonghae.profileservice.enumCustom.PetGender;
import com.meonghae.profileservice.enumCustom.PetType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pet extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @OneToMany(mappedBy = "pet", orphanRemoval = true)
  private List<Calendar> petCalendar = new ArrayList<>();

  @Column(nullable = false)
  private String userEmail;

  @Column(nullable = false)
  private PetType petType;

  @Column(nullable = false)
  private String petName;

  @Column(nullable = false)
  private PetGender petGender;

  @Column(nullable = false)
  private LocalDate petBirth;

  @Column(nullable = false)
  private String petSpecies;

  @Column
  private boolean hasImage = false;
  public void setHasImage() {
    this.hasImage = true;
  }

  public Pet (PetInfoRequestDto petInfoRequestDto, String userEmail){
    this.userEmail = userEmail;
    petType = petInfoRequestDto.getPetType();
    petName = petInfoRequestDto.getPetName();
    petGender = petInfoRequestDto.getPetGender();
    petBirth = petInfoRequestDto.getPetBirth();
    petSpecies = petInfoRequestDto.getPetSpecies();
  }

}
