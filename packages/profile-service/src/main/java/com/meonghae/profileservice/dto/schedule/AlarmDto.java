package com.meonghae.profileservice.dto.schedule;

import com.meonghae.profileservice.entity.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class AlarmDto {
    private String text;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp alarmTime;
    private String userEmail;

    public AlarmDto(Schedule schedule){
        this.alarmTime = Timestamp.valueOf(schedule.getAlarmTime());
        if (schedule.getScheduleType().getKey() < 10)
            this.text = schedule.getUserEmail()+"님, "+ schedule.getScheduleType().getTitle();
        else if(schedule.getScheduleType().getKey() > 10)
            this.text = schedule.getUserEmail()+"님, 오늘은 "+ schedule.getText()+ schedule.getScheduleType().getTitle()+" 일정이 있어요";
        this.userEmail = schedule.getUserEmail();
    }

    public AlarmDto(Schedule schedule, LocalDateTime intendedAlarmTime) {
        this.alarmTime = Timestamp.valueOf(intendedAlarmTime);
        if (schedule.getScheduleType().getKey() < 10)
            this.text = schedule.getUserEmail()+"님, "+ schedule.getScheduleType().getTitle();
        else if(schedule.getScheduleType().getKey() > 10)
            this.text = schedule.getUserEmail()+"님, 오늘은 "+ schedule.getText()+ schedule.getScheduleType().getTitle()+" 일정이 있어요";
        this.userEmail = schedule.getUserEmail();
    }
}
