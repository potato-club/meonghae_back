package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.calendar.AlarmDto;
import com.meonghae.profileservice.dto.fcm.FcmMessage;
import com.meonghae.profileservice.entity.Calendar;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RabbitService {
    private final RabbitTemplate rabbitTemplate;
    public void sendToRabbitMq(List<AlarmDto> alarms) {
        for (AlarmDto alarm : alarms) {
            Duration duration = Duration.between(LocalDateTime.now(),alarm.getAlarmTime().toLocalDateTime());
            long delayTime = duration.toMillis(); //밀리초는 long으로 반환됌
            //지연 속성 생성
            this.rabbitTemplate.convertAndSend(
                    "meonghae.exchange",
                    "lab303",
                    alarm,
                    message -> {
                        message.getMessageProperties().setDelay((int)delayTime);
                        return message;
                    }
            );
        }
    }
    public void sendToRabbitMq(AlarmDto alarmDto) {
        //시간 차이 계산
        Duration duration = Duration.between(LocalDateTime.now(),alarmDto.getAlarmTime().toLocalDateTime());
        long delayTime = duration.toMillis(); //밀리초는 long으로 반환됌
        //지연 속성 생성
        this.rabbitTemplate.convertAndSend(
                "meonghae.exchange",
                "lab303",
                alarmDto,
                message -> {
                    message.getMessageProperties().setDelay((int)delayTime);
                    return message;
                }
        );
    }
}
