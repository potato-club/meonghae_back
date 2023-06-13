package com.meonghae.profileservice.repository;

import com.meonghae.profileservice.entity.Pet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
  List<Pet> findByUserEmail(String userEmail);
  void deleteByUserEmail(String userEmail);
}
