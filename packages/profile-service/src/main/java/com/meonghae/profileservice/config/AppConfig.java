package com.meonghae.profileservice.config;

import com.meonghae.profileservice.dto.schedule.AlarmDto;
import com.meonghae.profileservice.dto.schedule.SchedulePreviewResponseDto;
import com.meonghae.profileservice.entity.QPet;
import com.meonghae.profileservice.entity.QSchedule;
import com.meonghae.profileservice.entity.Schedule;
import com.meonghae.profileservice.enumCustom.ScheduleCycleType;
import com.meonghae.profileservice.enumCustom.ScheduleType;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
                    LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                    LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

                    QSchedule qSchedule = QSchedule.schedule;
                    QPet qPet = QPet.pet;

                    List<Schedule> scheduleList = jpaQueryFactory
                            .selectFrom(qSchedule)
                            .leftJoin(qSchedule.pet,qPet)
                            .where(
                                   qSchedule.hasRepeat.isTrue().and(qSchedule.hasAlarm.isTrue().and(qSchedule.scheduleEndTime.goe(LocalDateTime.now())))
                                    .or(qSchedule.hasRepeat.isFalse()
                                            .and(qSchedule.scheduleTime.between(startOfDay,endOfDay))))
                            .fetch();

                    List<AlarmDto> alarmDtoList = new ArrayList<>();

                    for (Schedule schedule : scheduleList) {
                        if (schedule.getCycleType() == ScheduleCycleType.Month) {
                            if ((startOfDay.getMonthValue() - schedule.getAlarmTime().getMonthValue()) % schedule.getCycle() == 0
                                    && startOfDay.getDayOfMonth() == schedule.getAlarmTime().getDayOfMonth()) {

                               alarmDtoList.add(this.setIntendedAlarmTime(startOfDay,schedule));
                            }
                        }
                        else if (schedule.getCycleType() == ScheduleCycleType.Day) {
                            LocalDateTime nextScheduleTime = schedule.getAlarmTime()
                                    .plusDays(schedule.getCycle() * (ChronoUnit.DAYS.between(schedule.getAlarmTime(), LocalDateTime.now())) / schedule.getCycle());
                            if (startOfDay.getDayOfMonth() == nextScheduleTime.getDayOfMonth()) {
                                alarmDtoList.add(this.setIntendedAlarmTime(startOfDay,schedule));
                            }
                        }
                    }

                    rabbitService.sendToRabbitMq(alarmDtoList);
                },
                triggerContext -> new CronTrigger("0 0 1 1 1 *").nextExecutionTime(triggerContext) // 매일 자정에 실행
        );
    }
    private AlarmDto setIntendedAlarmTime(LocalDateTime startOfDay, Schedule schedule) {
        LocalDateTime intendedAlarmTime = startOfDay
                .withHour(schedule.getAlarmTime().getHour())
                .withMinute(schedule.getAlarmTime().getMinute());
        return new AlarmDto(schedule,intendedAlarmTime);
    }

    @Bean
    public Executor taskExecutor() {
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }
}
