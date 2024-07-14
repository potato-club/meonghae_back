package com.meonghae.profileservice.dto.schedule;

import com.meonghae.profileservice.entity.Schedule;
import com.meonghae.profileservice.enumcustom.ScheduleType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SchedulePreviewResponseDto {
    private Long id;
    private LocalDateTime scheduleDate;
    private String scheduleTitle;
    private String petName;

    public SchedulePreviewResponseDto (Schedule schedule, LocalDateTime intendedTime) {
        this.id = schedule.getId();
        this.scheduleDate = intendedTime;
        this.petName = schedule.getPet().getPetName();
        this.setScheduleText(schedule);
    }
    public SchedulePreviewResponseDto (Schedule schedule) {
        this.id = schedule.getId();
        this.scheduleDate = schedule.getScheduleTime();
        this.petName = schedule.getPet().getPetName();
        this.setScheduleText(schedule);
    }
    private void setScheduleText(Schedule schedule){
        if (schedule.getScheduleType() != ScheduleType.Custom) {
            this.scheduleTitle = schedule.getScheduleType().getTitle();
        } else if (schedule.getScheduleType() == ScheduleType.Custom) {
            this.scheduleTitle = schedule.getCustomScheduleTitle();
        }
    }
}
