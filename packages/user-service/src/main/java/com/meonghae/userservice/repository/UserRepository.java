package com.meonghae.userservice.repository;

import com.meonghae.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndDeleted(String email, boolean deleted);
    List<User> findByDeletedIsTrueAndModifiedDateBefore(LocalDateTime localDateTime);
}
