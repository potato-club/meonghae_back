package com.meonghae.userservice.mock;

import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.service.port.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FakeUserRepository implements UserRepository {

    private final List<User> data = Collections.synchronizedList(new ArrayList<>());
    private final Map<User, LocalDateTime> timeData = new HashMap<>();

    @Override
    public Optional<User> findByEmail(String email) {
        return data.stream().filter(item -> item.getEmail().equals(email)).findAny();
    }

    @Override
    public void save(User user) {
        if (user.getUid() == null) {
            User newUser = User.builder()
                    .uid(String.valueOf(UUID.randomUUID()))
                    .email(user.getEmail())
                    .age(user.getAge())
                    .birth(user.getBirth())
                    .nickname(user.getNickname())
                    .userRole(user.getUserRole())
                    .deleted(user.isDeleted())
                    .build();

            data.add(newUser);
            timeData.put(newUser, LocalDateTime.now());
        } else {
            data.removeIf(item -> Objects.equals(item.getUid(), user.getUid()));

            data.add(user);
            timeData.put(user, LocalDateTime.now());
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        Optional<User> value = data.stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();

        return value.isPresent();
    }

    @Override
    public boolean existsByEmailAndDeleted(String email, boolean deleted) {
        Optional<User> value = data.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();

        if (value.isEmpty()) {
            return false;
        }

        return value.get().isDeleted() == deleted;
    }

    @Override
    public List<User> findByDeletedIsTrueAndModifiedDateBefore(LocalDateTime localDateTime) {
        return data.stream()
                .filter(User::isDeleted)
                .filter(user -> timeData.get(user).isBefore(localDateTime))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll(List<User> list) {
        for (User user : list) {
            data.remove(user);
            timeData.remove(user);
        }
    }
}
