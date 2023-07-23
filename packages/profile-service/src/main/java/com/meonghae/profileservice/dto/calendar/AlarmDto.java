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
    private String token; //이거 어디서 받아오지
    private String text;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp alarmTime;

    public AlarmDto(Calendar calendar){
        this.alarmTime = Timestamp.valueOf(calendar.getAlarmTime());
        if (calendar.getScheduleType().getKey() < 10)
            this.text = calendar.getUserEmail()+"님, "+calendar.getScheduleType().getTitle();
        else if(calendar.getScheduleType().getKey() > 10)
            this.text = calendar.getUserEmail()+"님, 오늘은 "+calendar.getTitle()+calendar.getScheduleType().getTitle()+" 일정이 있어요";

        this.token = "플러터 사용자 식별 토큰";
    }
}
