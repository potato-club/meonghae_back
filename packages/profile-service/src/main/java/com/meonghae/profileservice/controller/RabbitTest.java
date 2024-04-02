package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.config.FcmConfig;
import com.meonghae.profileservice.config.RabbitmqFcmConsumer;
import com.meonghae.profileservice.dto.schedule.AlarmDto;
import com.meonghae.profileservice.service.RabbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RabbitTest {
    private final FcmConfig fcmConfig;

    @PostMapping("/profile/calendar/message")
    public String send(@RequestBody AlarmDto alarmDto) throws IOException {
        log.info(alarmDto.getUserEmail());
        log.info(alarmDto.getText());
        fcmConfig.sendMessageTo(alarmDto);
        return "send success";
    }
}
