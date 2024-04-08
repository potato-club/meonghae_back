package com.meonghae.userservice.infra.repository.fcmtoken;

import com.meonghae.userservice.domin.FCMToken.FCMToken;
import com.meonghae.userservice.infra.entity.FCMTokenEntity;
import com.meonghae.userservice.service.port.FCMTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FCMTokenRepositoryImpl implements FCMTokenRepository {

    private final FCMTokenJpaRepository fcmTokenRepository;

    @Override
    public FCMToken findByEmail(String email) {
        FCMTokenEntity fcmToken = fcmTokenRepository.findByEmail(email);

        if (fcmToken == null) {
            return null;
        }

        return fcmToken.toModel();
    }

    @Override
    public void save(FCMToken fcmToken) {
        fcmTokenRepository.save(FCMTokenEntity.from(fcmToken));
    }

    @Override
    public void deleteByEmail(String email) {
        fcmTokenRepository.deleteByEmail(email);
    }
}
