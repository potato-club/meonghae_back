package com.meonghae.profileservice.dto.calendar;

import com.meonghae.profileservice.entity.RecurringSchedule;
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
    this.scheduleType = schedule.getScheduleType();
    this.scheduleTime = schedule.getScheduleTime();
    this.alarmTime = schedule.getAlarmTime().toString();
    this.text = schedule.getText();
  }
  public ScheduleResponseDTO(RecurringSchedule recurringSchedule, LocalDateTime intendedTime) {
    this.id = recurringSchedule.getId();
    this.petName = recurringSchedule.getPet().getPetName();
    this.scheduleType = recurringSchedule.getScheduleType();
    this.scheduleTime = intendedTime;
    this.alarmTime = recurringSchedule.getAlarmTime().toString();
    this.text = recurringSchedule.getText();

  }
  public ScheduleResponseDTO(RecurringSchedule recurringSchedule) {
    this.id = recurringSchedule.getId();
    this.petName = recurringSchedule.getPet().getPetName();
    this.scheduleType = recurringSchedule.getScheduleType();
    this.scheduleTime = recurringSchedule.getScheduleTime();
    this.alarmTime = recurringSchedule.getAlarmTime().toString();
    this.text = recurringSchedule.getText();
  }
}
