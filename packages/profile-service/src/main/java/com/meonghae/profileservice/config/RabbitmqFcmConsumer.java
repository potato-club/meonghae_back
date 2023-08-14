package com.meonghae.profileservice.config;

import com.meonghae.profileservice.dto.schedule.AlarmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitmqFcmConsumer {
    private final FcmConfig fcmConfig;

    @RabbitListener(queues = "meonghae.queue")
    public void receive(AlarmDto alarmDto) throws IOException {
        log.info(alarmDto.getText());
        log.info(alarmDto.getAlarmTime().toString());

        fcmConfig.sendMessageTo(alarmDto);
    }
}
