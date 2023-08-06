package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.calendar.ScheduleRequestDTO;
import com.meonghae.profileservice.dto.calendar.ScheduleResponseDTO;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.BadRequestException;
import com.meonghae.profileservice.service.ScheduleService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile/calendar")
@Api(value = "Schedule Controller", tags = "유저 / 반려동물 관련 일정 서비스 API")
@RequiredArgsConstructor
public class ScheduleController {
  private final ScheduleService scheduleService;

  @Operation(summary = "일정 list 반환 Limit 30개")
  @GetMapping("/preview")
  public List<ScheduleResponseDTO> getProfileSchedule(@RequestHeader("Authorization") String token) {
    return scheduleService.getProfileSchedule(token);
  }

   @Operation(summary = "년,월 입력 시 달력반환, 년,월,일 입력시 하루 일정반환")
  @GetMapping("")
  public List<ScheduleResponseDTO> getSchedule(
           @RequestParam("year")int year,
           @RequestParam("month")int month,
           @RequestParam(value = "day", required = false)Integer day,
           @RequestHeader("Authorization") String token) {
       if (day != null) {
           LocalDateTime startOfDate = LocalDateTime.of(year, month, day, 0, 0, 0);
           return scheduleService.getSchedule(startOfDate, token);
       } else {
           LocalDate startOfDate = LocalDate.of(year, month, 1);
           return scheduleService.getMonthSchedule(startOfDate, token);
       }
  }

  @Operation(summary = "일정 검색 API")
  @GetMapping("/find")
  public List<ScheduleResponseDTO> getScheduleOfFindByText(@RequestParam("key") String key, @RequestHeader("Authorization") String token){
      if (key == null){
          throw new BadRequestException(ErrorCode.RUNTIME_EXCEPTION,ErrorCode.RUNTIME_EXCEPTION.getMessage());
      }
    return scheduleService.getScheduleOfFindByText(key, token);
  }

  @Operation(summary = "일정 추가 API")
  @PostMapping
  public String addSchedule(
          @RequestBody ScheduleRequestDTO scheduleRequestDTO, @RequestHeader("Authorization") String token) {
    return scheduleService.addSchedule(scheduleRequestDTO, token);
  }
   @Operation(summary = "일정 수정 API")
  @PutMapping("/{id}")
  public String updateSchedule(
      @PathVariable Long id,
      @RequestBody ScheduleRequestDTO scheduleRequestDTO) {
    return scheduleService.updateSchedule(id, scheduleRequestDTO);
  }
   @Operation(summary = "일정 삭제 API")
  @DeleteMapping("/{id}")
  public String deleteSchedule(@PathVariable Long id) {
    return scheduleService.deleteSchedule(id);
  }
}
