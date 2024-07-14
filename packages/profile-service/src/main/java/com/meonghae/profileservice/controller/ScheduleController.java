package com.meonghae.profileservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meonghae.profileservice.dto.schedule.*;
import com.meonghae.profileservice.enumcustom.ScheduleType;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.BadRequestException;
import com.meonghae.profileservice.service.ScheduleService;

import java.time.LocalDate;
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
    //id 받으면 단일 리턴
  @GetMapping("/detail/{id}")
  public ScheduleResponseDTO getSchedule(@PathVariable Long id, @RequestHeader("Authorization") String token){

    return scheduleService.getSchedule(id,token);
  }

  @Operation(summary = "일정 list 반환 Limit 5개")
  @GetMapping("/preview")
  public List<SchedulePreviewResponseDto> getProfileSchedule(@RequestHeader("Authorization") String token) {

    return scheduleService.getProfileSchedule(token);
  }

  @Operation(summary = "년,월 입력 시 달력 전체에 뿌려져있는 일정들의 ID를 반환")
  @GetMapping("/month")
  public List<SimpleMonthSchedule> getMonthSchedule(@RequestParam int year, @RequestParam int month,
                                               @RequestHeader("Authorization") String token) throws JsonProcessingException {

    LocalDate startOfDate = LocalDate.of(year, month, 1);

    return scheduleService.getMonthOfSchedule(startOfDate, token);
  }

  @Operation(summary = "년,월 입력 시 달력반환, 년,월,일 입력시 하루 일정반환")
  @GetMapping("/day")
  public List<ScheduleResponseDTO> getSchedule(
          @RequestBody ScheduleOfDayRequestDto scheduleOfDayRequestDto,
          @RequestHeader("Authorization") String token) {

      LocalDate targetDate = LocalDate.of(scheduleOfDayRequestDto.getYear(), scheduleOfDayRequestDto.getMonth(), scheduleOfDayRequestDto.getDay());
      return scheduleService.getDayOfSchedule(targetDate, token, scheduleOfDayRequestDto.getScheduleId());
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
    if(scheduleRequestDTO.getScheduleType().equals(ScheduleType.Test) && scheduleRequestDTO.isHasAlarm()) {
      return scheduleService.fcmTest(scheduleRequestDTO, token);
    }
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
