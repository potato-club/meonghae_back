package com.meonghae.profileservice.entity;

import com.meonghae.profileservice.dto.calendar.CalendarRequestDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Calendar extends BaseTimeEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String userEmail;

  @ManyToOne
  @JoinColumn(name = "petEntityId")
  private PetEntity petEntity;

  @Column(nullable = false)
  private String text;

  @Column(nullable = false)
  private LocalDateTime scheduleTime;


  public void update(CalendarRequestDTO calendarRequestDTO, PetEntity petEntity) {
    LocalTime scheduleTime =
        LocalTime.of(calendarRequestDTO.getHour(), calendarRequestDTO.getMinute());
    LocalDate scheduleDate =
        LocalDate.of(
            calendarRequestDTO.getYear(),
            calendarRequestDTO.getMonth(),
            calendarRequestDTO.getDay());
    this.petEntity = petEntity;
    this.text = calendarRequestDTO.getText();
    this.scheduleTime = LocalDateTime.of(scheduleDate, scheduleTime);
  }
}
