package com.meonghae.userservice.infra.repository.user;

import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.infra.entity.UserEntity;
import com.meonghae.userservice.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserEntity::toModel);
    }

    @Override
    public void save(User user) {
        userRepository.save(UserEntity.from(user));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndDeleted(String email, boolean deleted) {
        return userRepository.existsByEmailAndDeleted(email, deleted);
    }

    @Override
    public List<User> findByDeletedIsTrueAndModifiedDateBefore(LocalDateTime localDateTime) {
        return userRepository.findByDeletedIsTrueAndModifiedDateBefore(localDateTime)
                .stream()
                .map(UserEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll(List<User> list) {
        List<UserEntity> entityList = list.stream()
                .map(UserEntity::from)
                .collect(Collectors.toList());

        userRepository.deleteAll(entityList);
    }
}
