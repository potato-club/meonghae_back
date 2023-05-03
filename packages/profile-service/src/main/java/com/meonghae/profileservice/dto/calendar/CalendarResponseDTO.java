package com.meonghae.profileservice.dto.calendar;

import com.meonghae.profileservice.entity.Calendar;
import com.meonghae.profileservice.entity.PetEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarResponseDTO {
  private Long id;
  private PetEntity petEntity;
  private int year;
  private int month;
  private int day;
  private int hour;
  private int minute;
  private String text;

  public CalendarResponseDTO(Calendar calendar) {
    this.id = calendar.getId();
    this.petEntity = calendar.getPetEntity();
    this.year = calendar.getScheduleTime().getYear();
    this.month = calendar.getScheduleTime().getMonthValue();
    this.day = calendar.getScheduleTime().getDayOfMonth();
    this.hour = calendar.getScheduleTime().getHour();
    this.minute = calendar.getScheduleTime().getMinute();
    this.text = calendar.getText();
  }
}
