package com.meonghae.userservice.mock;

import com.meonghae.userservice.domin.FCMToken.FCMToken;
import com.meonghae.userservice.service.port.FCMTokenRepository;

import java.util.*;

public class FakeFCMTokenRepository implements FCMTokenRepository {

    private final List<FCMToken> data = Collections.synchronizedList(new ArrayList<>());

    @Override
    public FCMToken findByEmail(String email) {
        Optional<FCMToken> token = data.stream().filter(item -> item.getEmail().equals(email)).findAny();
        return token.orElse(null);
    }

    @Override
    public void save(FCMToken fcmToken) {
        if (fcmToken.getId() == null) {
            FCMToken newUser = FCMToken.builder()
                    .id(1L)
                    .email(fcmToken.getEmail())
                    .token(fcmToken.getToken())
                    .build();

            data.add(newUser);
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), fcmToken.getId()));
            data.add(fcmToken);
        }
    }

    @Override
    public void deleteByEmail(String email) {
        Optional<FCMToken> value = data.stream()
                .filter(fcmToken -> fcmToken.getEmail().equals(email))
                .findAny();

        value.ifPresent(data::remove);
    }
}
