package com.meonghae.profileservice.dto.calendar;

import com.meonghae.profileservice.entity.Pet;
import com.meonghae.profileservice.entity.Schedule;
import com.meonghae.profileservice.enumCustom.ScheduleCycleType;
import com.meonghae.profileservice.enumCustom.ScheduleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

@Getter
public class ScheduleRequestDTO {
  @ApiModelProperty(notes = "반려동물 id", example = "LONG", required = true)
  private Long petId;
  @ApiModelProperty(notes = "일정 지정 타입", example = "합의된 일정 or Custom(유저 커스텀)")
  private ScheduleType scheduleType;
  @ApiModelProperty(notes = "자동 반복 여부", example = "T or F")
  private boolean hasRepeat;
  @ApiModelProperty(notes = "반복시간 타입설정", example = "Month or Day")
  private ScheduleCycleType cycleType;
  @ApiModelProperty(notes = "반복주기 설정", example = "Month or Day")
  private int cycle;
  @ApiModelProperty(notes = "일정 날짜와 시간", example = "2023-01-01THH:mm", required = true)
  private LocalDateTime scheduleTime;
  @ApiModelProperty(notes = "알림 지정 시간", example = "2023-01-01THH:mm")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime alarmTime;
  @ApiModelProperty(notes = "일정 정보", example = "예방주사 맞는 날", required = true)
  private String text;

}
