package com.meonghae.profileservice.repository;

import com.meonghae.profileservice.entity.PetEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<PetEntity, Long> {
  List<PetEntity> findByUserNameOrderById(String userName);
}
