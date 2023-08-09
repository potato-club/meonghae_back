package com.meonghae.profileservice.entity;

import com.meonghae.profileservice.dto.calendar.ScheduleRequestDTO;

import java.time.LocalDateTime;
import javax.persistence.*;

import com.meonghae.profileservice.enumCustom.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Schedule extends BaseTimeEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String userEmail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pet_id",nullable = false)
  private Pet pet;
  @Column
  private ScheduleType scheduleType;
  @Column(nullable = false)
  private LocalDateTime scheduleTime;
  @Column
  private LocalDateTime alarmTime;
  @Column(nullable = false)
  private String text;

  public Schedule(Pet pet, String userEmail,ScheduleRequestDTO scheduleRequestDTO) {
    this.pet = pet;
    this.userEmail = userEmail;
    this.scheduleTime = scheduleRequestDTO.getScheduleTime();
    this.scheduleType = scheduleRequestDTO.getScheduleType();
    this.alarmTime = scheduleRequestDTO.getAlarmTime();
    this.text = scheduleRequestDTO.getText();
  }


  public void update(ScheduleRequestDTO scheduleRequestDTO, Pet pet) {
    this.pet = pet;
    this.scheduleType = scheduleRequestDTO.getScheduleType();
    this.text = scheduleRequestDTO.getText();
    this.scheduleTime = scheduleRequestDTO.getScheduleTime();
    this.alarmTime = scheduleRequestDTO.getAlarmTime();
  }
}
