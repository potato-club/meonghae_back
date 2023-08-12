package com.meonghae.communityservice.Controller;

import com.meonghae.communityservice.Dto.FcmDto.FcmDto;
import com.meonghae.communityservice.Service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmController {
    private RedisService redisService;

    @PostMapping("")
    public String saveFcmToken(@RequestBody FcmDto fcmDto) {
        redisService.saveFcmToken(fcmDto);
        return "저장 완료";
    }
}
