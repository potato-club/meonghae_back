package com.meonghae.userservice.service.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

@FeignClient(name = "profile-service")
public interface PetServiceClient {

    @DeleteMapping(value = "/profile/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> deletedByUserEmail(@RequestPart String email);

    @PostMapping(value = "/profile/exchange/token", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)  // FCM Token 관리용
    void getReviseFcmToken(@RequestPart String email, @RequestPart String fcmToken);
}
