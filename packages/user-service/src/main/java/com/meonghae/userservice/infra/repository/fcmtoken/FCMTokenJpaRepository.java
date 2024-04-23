package com.meonghae.userservice.infra.repository.fcmtoken;

import com.meonghae.userservice.infra.entity.FCMTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FCMTokenJpaRepository extends JpaRepository<FCMTokenEntity, Long> {

    FCMTokenEntity findByEmail(String email);

    void deleteByEmail(String email);
}
