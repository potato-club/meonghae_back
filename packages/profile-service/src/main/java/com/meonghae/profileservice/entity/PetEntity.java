package com.meonghae.profileservice.entity;

import com.meonghae.profileservice.enumCustom.PetGender;
import com.meonghae.profileservice.enumCustom.PetType;
import java.util.Date;
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
public class PetEntity extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(nullable = false)
  private String userId;

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
