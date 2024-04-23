package com.meonghae.userservice.infra.repository.user;

import com.meonghae.userservice.infra.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndDeleted(String email, boolean deleted);

    List<UserEntity> findByDeletedIsTrueAndModifiedDateBefore(LocalDateTime localDateTime);
}
