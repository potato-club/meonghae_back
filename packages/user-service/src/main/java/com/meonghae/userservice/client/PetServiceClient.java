package com.meonghae.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;

@FeignClient(name = "profile-service")
public interface PetServiceClient {

    @DeleteMapping("/users")
    ResponseEntity<String> deletedByUserEmail(@RequestPart String email);

    @GetMapping("/profile/exchange/token")  // FCM Token 관리용
    void getReviseFcmToken(@RequestPart String email, @RequestPart String fcmToken);
}
