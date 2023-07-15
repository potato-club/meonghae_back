package com.meonghae.profileservice.config;

import com.meonghae.profileservice.dto.calendar.AlarmDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitmqTestCosumer {
    @RabbitListener(queues = "meonghae.queue")
    public void receive(AlarmDto alarmDto){
        log.info(alarmDto.getUserEmail());
        log.info(alarmDto.getText());
        log.info(alarmDto.getAlarmTime().toString());

    }
}
