package com.meonghae.profileservice.dto.calendar;

import com.meonghae.profileservice.entity.Schedule;
import com.meonghae.profileservice.enumCustom.ScheduleType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleResponseDTO {
  private Long id;
  private String petName;
  private String title;
  private ScheduleType scheduleType;
  private LocalDateTime scheduleTime;
  private String alarmTime;
  private String text;

  public ScheduleResponseDTO(Schedule schedule) {
    this.id = schedule.getId();
    this.petName = schedule.getPet().getPetName();
    this.title = schedule.getTitle();
    this.scheduleType = schedule.getScheduleType();
    this.scheduleTime = schedule.getScheduleTime();
    this.alarmTime = schedule.getAlarmTime().toString();
    this.text = schedule.getText();
  }
}
