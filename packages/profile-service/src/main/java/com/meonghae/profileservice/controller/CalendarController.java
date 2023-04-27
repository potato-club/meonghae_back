package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.calendar.CalendarRequestDTO;
import com.meonghae.profileservice.dto.calendar.CalendarResponseDTO;
import com.meonghae.profileservice.service.CalendarService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/profile/calendar")
@Api(value = "Calendar Controller", tags = "유저 일정 컨트롤러")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @Operation(summary = "일정 list 반환 Limit 30개")
    @GetMapping("/preview")
    public List<CalendarResponseDTO> getProfileSchedule(HttpServletRequest request){
        return calendarService.getProfileSchedule(request);
    }
    //== param { year , month }
    @Operation(summary = "년도와 달을 입력시 해당 달의 일정들을 반환")
    @GetMapping("/month")
    public List<CalendarResponseDTO> getMonthSechedule(@RequestBody CalendarRequestDTO calendarRequestDTO, HttpServletRequest request){
        return calendarService.getMonthSechedule(calendarRequestDTO,request);
    }
    @Operation(summary = "해당 일에 대한 일정들을 반환")
    //== param { year , month , day}
    @GetMapping("/day")
    public List<CalendarResponseDTO> getSchedule(@RequestBody CalendarRequestDTO calendarRequestDTO, HttpServletRequest request){
        return calendarService.getSchedule(calendarRequestDTO,request);
    }
    @Operation(summary = "일정 추가 API")
    @PostMapping
    public String addSchedule (@RequestBody CalendarRequestDTO calendarRequestDTO,HttpServletRequest request){
        return calendarService.addSchedule(calendarRequestDTO,request);
    }
    @Operation(summary = "일정 수정 API")
    @PutMapping("/{id}")
    public String updateSchedule (@PathVariable Long id,@RequestBody CalendarRequestDTO calendarRequestDTO, HttpServletRequest request){
        return calendarService.updateSchedule(id,calendarRequestDTO,request);
    }
    @Operation(summary = "일정 삭제 API")
    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable Long id,HttpServletRequest request){
        return calendarService.deleteSchedule(id, request);
    }
}
