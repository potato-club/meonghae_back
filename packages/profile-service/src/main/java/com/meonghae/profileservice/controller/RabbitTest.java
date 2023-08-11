package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.schedule.AlarmDto;
import com.meonghae.profileservice.service.RabbitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RabbitTest {
    private final RabbitService rabbitService;

    @PostMapping("/profile/calendar/message")
    public String send(@RequestBody AlarmDto alarmDto){
        rabbitService.sendToRabbitMq(alarmDto);
        return "send success";
    }
}
