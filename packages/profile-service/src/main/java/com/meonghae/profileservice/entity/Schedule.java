package com.meonghae.profileservice.entity;

import com.meonghae.profileservice.dto.schedule.ScheduleRequestDTO;

import java.time.LocalDateTime;
import javax.persistence.*;

import com.meonghae.profileservice.enumCustom.ScheduleCycleType;
import com.meonghae.profileservice.enumCustom.ScheduleType;
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
  @Column(nullable = false)
  private ScheduleType scheduleType;
  @Column(nullable = false)
  private boolean hasRepeat;
  @Column
  private ScheduleCycleType cycleType;
  @Column
  private int cycleCount;
  @Column
  private int cycle;
  @Column(nullable = false)
  private LocalDateTime scheduleTime;
  @Column
  private LocalDateTime alarmTime;
  @Column
  private LocalDateTime scheduleEndTime;
  @Column(nullable = false)
  private String text;

  public Schedule(Pet pet, String userEmail,LocalDateTime scheduleEndTime, ScheduleRequestDTO scheduleRequestDTO) {
    this.pet = pet;
    this.userEmail = userEmail;
    this.scheduleType = scheduleRequestDTO.getScheduleType();
    this.hasRepeat = scheduleRequestDTO.isHasRepeat();
    this.cycleType = scheduleRequestDTO.getCycleType();
    this.cycleCount = scheduleRequestDTO.getCycleCount();
    this.cycle = scheduleRequestDTO.getCycle();
    this.scheduleTime = scheduleRequestDTO.getScheduleTime();
    this.alarmTime = scheduleRequestDTO.getAlarmTime();
    this.scheduleEndTime = scheduleEndTime;
    this.text = scheduleRequestDTO.getText();
  }


  public void update(ScheduleRequestDTO scheduleRequestDTO, Pet pet, LocalDateTime scheduleEndTime) {
    this.pet = pet;
    this.scheduleType = scheduleRequestDTO.getScheduleType();
    this.hasRepeat = scheduleRequestDTO.isHasRepeat();
    this.cycleType = scheduleRequestDTO.getCycleType();
    this.cycle = scheduleRequestDTO.getCycle();
    this.scheduleTime = scheduleRequestDTO.getScheduleTime();
    this.alarmTime = scheduleRequestDTO.getAlarmTime();
    this.scheduleEndTime = scheduleEndTime;
    this.text = scheduleRequestDTO.getText();
  }
}
