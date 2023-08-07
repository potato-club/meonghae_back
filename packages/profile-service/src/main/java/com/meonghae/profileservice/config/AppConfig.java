package com.meonghae.profileservice.config;

import com.meonghae.profileservice.dto.calendar.AlarmDto;
import com.meonghae.profileservice.entity.QSchedule;
import com.meonghae.profileservice.entity.Schedule;
import com.meonghae.profileservice.service.RabbitService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class AppConfig implements SchedulingConfigurer {

    private final RabbitService rabbitService;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                () -> {
                    //금일의 일정에 해당되는 것들을 긁어오고 amqp에 보내기
                    LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                    LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

                    QSchedule qSchedule = QSchedule.schedule;
                    List<Schedule> result = jpaQueryFactory.select(qSchedule)
                            .from(qSchedule)
                            .where(qSchedule.alarmTime.between(startOfDay, endOfDay))
                            .orderBy(qSchedule.alarmTime.asc())
                            .fetch();


                    List<AlarmDto> alarms = result.stream().map(AlarmDto::new).collect(Collectors.toList());
                    rabbitService.sendToRabbitMq(alarms);

                },
                triggerContext -> new CronTrigger("0 0 1 1 * *").nextExecutionTime(triggerContext) // 매일 자정에 실행
        );
    }

    @Bean
    public Executor taskExecutor() {
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }
}
