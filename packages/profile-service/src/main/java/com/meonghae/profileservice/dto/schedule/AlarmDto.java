package com.meonghae.profileservice.dto.schedule;

import com.meonghae.profileservice.entity.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;


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

}
