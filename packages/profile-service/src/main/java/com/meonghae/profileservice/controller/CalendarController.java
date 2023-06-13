package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.calendar.CalendarRequestDTO;
import com.meonghae.profileservice.dto.calendar.CalendarResponseDTO;
import com.meonghae.profileservice.service.CalendarService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile/calendar")
@Api(value = "Calendar Controller", tags = "유저 / 반려동물 관련 일정 서비스 API")
@RequiredArgsConstructor
public class CalendarController {
  private final CalendarService calendarService;

  @Operation(summary = "일정 list 반환 Limit 30개")
  @GetMapping("/preview")
  public List<CalendarResponseDTO> getProfileSchedule(@RequestHeader("Authorization") String token) {
    return calendarService.getProfileSchedule(token);
  }

   @Operation(summary = "년,월 입력 시 달력반환, 년,월,일 입력시 하루 일정반환")
  @GetMapping("")
  public List<CalendarResponseDTO> getSchedule(
           @RequestParam("year")int year,
           @RequestParam("month")int month,
           @RequestParam(value = "day", required = false)Integer day,
           @RequestHeader("Authorization") String token) {
       if (day != null) {
           LocalDateTime startOfDate = LocalDateTime.of(year, month, day, 0, 0, 0);
           return calendarService.getSchedule(startOfDate, token);
       } else {
           LocalDate startOfDate = LocalDate.of(year, month, 1);
           return calendarService.getMonthSchedule(startOfDate, token);
       }
  }

  @Operation(summary = "일정 검색 API")
  @GetMapping("/find")
  public List<CalendarResponseDTO> getScheduleOfFindByText(@RequestParam("key") String key){
    return calendarService.getScheduleOfFindByText(key);
  }

  @Operation(summary = "일정 추가 API")
  @PostMapping
  public String addSchedule(
      @RequestBody CalendarRequestDTO calendarRequestDTO, @RequestHeader("Authorization") String token) {
    return calendarService.addSchedule(calendarRequestDTO, token);
  }
   @Operation(summary = "일정 수정 API")
  @PutMapping("/{id}")
  public String updateSchedule(
      @PathVariable Long id,
      @RequestBody CalendarRequestDTO calendarRequestDTO) {
    return calendarService.updateSchedule(id, calendarRequestDTO);
  }
   @Operation(summary = "일정 삭제 API")
  @DeleteMapping("/{id}")
  public String deleteSchedule(@PathVariable Long id) {
    return calendarService.deleteSchedule(id);
  }
}
