package com.meonghae.userservice.repository;

import com.meonghae.userservice.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {

    FCMToken findByEmail(String email);

    void deleteByEmail(String email);
}
