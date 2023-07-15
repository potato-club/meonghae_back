package com.meonghae.profileservice.dto.calendar;

import com.meonghae.profileservice.entity.Calendar;
import com.meonghae.profileservice.enumCustom.ScheduleType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CalendarResponseDTO {
  private Long id;
  private String petName;
  private String title;
  private ScheduleType scheduleType;
  private LocalDateTime scheduleTime;
  private String alarmTime;
  private String text;

  public CalendarResponseDTO(Calendar calendar) {
    this.id = calendar.getId();
    this.petName = calendar.getPet().getPetName();
    this.title = calendar.getTitle();
    this.scheduleType = calendar.getScheduleType();
    this.scheduleTime = calendar.getScheduleTime();
    this.alarmTime = calendar.getAlarmTime().toString();
    this.text = calendar.getText();
  }
}
