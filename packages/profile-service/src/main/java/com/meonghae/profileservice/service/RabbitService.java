package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.calendar.AlarmDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RabbitService {
    private final RabbitTemplate rabbitTemplate;
    public void sendToRabbitMq(List<AlarmDto> alarms) {
        for (AlarmDto alarm : alarms) {
            this.rabbitTemplate.convertAndSend("meonghae.exchange", "lab303", alarm);
        }
    }
    public void sendToRabbitMq(AlarmDto alarmDto) {
        this.rabbitTemplate.convertAndSend("meonghae.exchange", "lab303", alarmDto);
    }
}
