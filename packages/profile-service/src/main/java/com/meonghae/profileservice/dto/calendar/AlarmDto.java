package com.meonghae.profileservice.dto.calendar;

import com.meonghae.profileservice.entity.Calendar;
import com.meonghae.profileservice.enumCustom.ScheduleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;


@Getter
@NoArgsConstructor
public class AlarmDto {
    private String userEmail;
    private String title;
    private ScheduleType scheduleType;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp alarmTime;

    public AlarmDto(Calendar calendar){
        this.alarmTime = Timestamp.valueOf(calendar.getAlarmTime());
        this.title = calendar.getTitle();
        this.scheduleType = calendar.getScheduleType();
        this.userEmail = calendar.getUserEmail();
    }
}
