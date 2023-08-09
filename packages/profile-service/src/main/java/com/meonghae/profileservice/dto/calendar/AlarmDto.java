package com.meonghae.profileservice.dto.calendar;

import com.meonghae.profileservice.entity.RecurringSchedule;
import com.meonghae.profileservice.entity.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class AlarmDto {
    private String token; //이거 어디서 받아오지
    private String text;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp alarmTime;

    public AlarmDto(Schedule schedule){
        this.alarmTime = Timestamp.valueOf(schedule.getAlarmTime());
        if (schedule.getScheduleType().getKey() < 10)
            this.text = schedule.getUserEmail()+"님, "+ schedule.getScheduleType().getTitle();
        else if(schedule.getScheduleType().getKey() > 10)
            this.text = schedule.getUserEmail()+"님, 오늘은 "+ schedule.getText()+ schedule.getScheduleType().getTitle()+" 일정이 있어요";

        this.token = "플러터 사용자 식별 토큰";
    }

    public AlarmDto(RecurringSchedule recurringSchedule, LocalDateTime intendedTime) {
        this.alarmTime = Timestamp.valueOf(intendedTime);
        if (recurringSchedule.getScheduleType().getKey() < 10)
            this.text = recurringSchedule.getUserEmail()+"님, "+ recurringSchedule.getScheduleType().getTitle();
        else if(recurringSchedule.getScheduleType().getKey() > 10)
            this.text = recurringSchedule.getUserEmail()+"님, 오늘은 "+ recurringSchedule.getText()+", "+ recurringSchedule.getScheduleType().getTitle()+" 일정이 있어요";

        this.token = "플러터 사용자 식별 토큰";
    }
}
