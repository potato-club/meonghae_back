package com.meonghae.profileservice.dto.schedule;


import com.meonghae.profileservice.entity.Schedule;
import com.meonghae.profileservice.enumCustom.ScheduleCycleType;
import com.meonghae.profileservice.enumCustom.ScheduleType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleResponseDTO {
  private Long id;
  private String petName;
  private Long petId;
  private ScheduleType scheduleType;
  private String customScheduleTitle;
  private boolean hasRepeat;
  private ScheduleCycleType cycleType;
  private int cycleCount;
  private int cycle;
  private LocalDateTime scheduleTime;
  private boolean hasAlarm;
  private LocalDateTime alarmTime;
  private String text;

  public ScheduleResponseDTO(Schedule schedule) {
    this.id = schedule.getId();
    this.petName = schedule.getPet().getPetName();
    this.petId = schedule.getPet().getId();
    this.scheduleType = schedule.getScheduleType();
    this.customScheduleTitle = schedule.getCustomScheduleTitle();
    this.hasRepeat = schedule.isHasRepeat();
    this.scheduleTime = schedule.getScheduleTime();
    this.cycleType = schedule.getCycleType();
    this.cycleCount = schedule.getCycleCount();
    this.hasAlarm = schedule.isHasAlarm();
    this.alarmTime = schedule.getAlarmTime();
    this.text = schedule.getText();
  }

  public ScheduleResponseDTO(Schedule schedule, LocalDateTime intendedTime) {
    this.id = schedule.getId();
    this.petName = schedule.getPet().getPetName();
    this.petId = schedule.getPet().getId();
    this.scheduleType = schedule.getScheduleType();
    this.customScheduleTitle = schedule.getCustomScheduleTitle();
    this.hasRepeat = schedule.isHasRepeat();
    this.scheduleTime = intendedTime;
    this.cycleType = schedule.getCycleType();
    this.cycleCount = schedule.getCycleCount();
    this.hasAlarm = schedule.isHasAlarm();
    this.alarmTime = schedule.getAlarmTime();
    this.text = schedule.getText();
  }
}
