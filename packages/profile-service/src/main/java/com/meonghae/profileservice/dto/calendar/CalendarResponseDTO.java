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
  private String petName;
  private String scheduleTime;
  private String text;

  public CalendarResponseDTO(Calendar calendar) {
    this.id = calendar.getId();
    this.petName = calendar.getPet().getPetName();
    this.scheduleTime = calendar.getScheduleTime().toString();
    this.text = calendar.getText();
  }
}
