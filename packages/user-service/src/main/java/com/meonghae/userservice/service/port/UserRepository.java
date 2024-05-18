package com.meonghae.userservice.service.port;

import com.meonghae.userservice.domin.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    void save(User user);

    boolean existsByEmail(String email);

    boolean existsByEmailAndDeleted(String email, boolean deleted);

    List<User> findByDeletedIsTrueAndModifiedDateBefore(LocalDateTime localDateTime);

    void deleteAll(List<User> list);
}
