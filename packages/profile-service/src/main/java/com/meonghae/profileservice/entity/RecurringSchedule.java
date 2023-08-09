package com.meonghae.profileservice.entity;

import com.meonghae.profileservice.dto.calendar.ScheduleRequestDTO;
import com.meonghae.profileservice.enumCustom.ScheduleType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class RecurringSchedule extends BaseTimeEntity{

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

    public RecurringSchedule(Pet pet, String userEmail, ScheduleRequestDTO scheduleRequestDTO) {
        this.pet = pet;
        this.userEmail = userEmail;
        this.scheduleTime = scheduleRequestDTO.getScheduleTime();
        this.scheduleType = scheduleRequestDTO.getScheduleType();
        this.alarmTime = scheduleRequestDTO.getAlarmTime();
        this.text = scheduleRequestDTO.getText();
    }
}
