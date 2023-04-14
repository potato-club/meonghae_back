package com.meonghae.profileservice.entity;

import com.meonghae.profileservice.dto.calendar.CalendarRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String user;
    @ManyToOne
    @JoinColumn(name = "petEntityId")
    private PetEntity petEntity;
    @Column(nullable = false)
    private String text;
    @Column(nullable = false)
    private LocalDateTime scheduleTime;
    private LocalDateTime createTime;
    private LocalDateTime modifiedDate;

    public void update(CalendarRequestDTO calendarRequestDTO, PetEntity petEntity){
        LocalTime scheduleTime = LocalTime.of(calendarRequestDTO.getHour(), calendarRequestDTO.getMinute());
        LocalDate scheduleDate = LocalDate.of(calendarRequestDTO.getYear(), calendarRequestDTO.getMonth(), calendarRequestDTO.getDay());
        this.petEntity = petEntity;
        this.text = calendarRequestDTO.getText();
        this.scheduleTime = LocalDateTime.of(scheduleDate, scheduleTime);
        this.modifiedDate = LocalDateTime.now();
    }
}
