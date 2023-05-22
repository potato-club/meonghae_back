package com.meonghae.profileservice.dto.calendar;

import com.meonghae.profileservice.entity.Calendar;
import com.meonghae.profileservice.entity.Pet;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CalendarResponseDTO {
  private Long id;
  private Pet pet;
  private LocalDateTime scheduleTime;
  private String text;

  public CalendarResponseDTO(Calendar calendar) {
    this.id = calendar.getId();
    this.pet = calendar.getPet();
    this.scheduleTime = calendar.getScheduleTime();
    this.text = calendar.getText();
  }
}
