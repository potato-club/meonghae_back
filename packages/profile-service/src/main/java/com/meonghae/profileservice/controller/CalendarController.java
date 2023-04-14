package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.calendar.CalendarRequestDTO;
import com.meonghae.profileservice.dto.calendar.CalendarResponseDTO;
import com.meonghae.profileservice.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/profile/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;
    //== 이 주석은 swagger 적용때 참고

    @GetMapping("/preview")
    public List<CalendarResponseDTO> getProfileSchedule(HttpServletRequest request){
        return calendarService.getProfileSchedule(request);
    }
    //== param { year , month }
    @GetMapping("/month")
    public List<CalendarResponseDTO> getMonthSechedule(@RequestBody CalendarRequestDTO calendarRequestDTO, HttpServletRequest request){
        return calendarService.getMonthSechedule(calendarRequestDTO,request);
    }
    //== param { year , month , day}
    @GetMapping("/day")
    public List<CalendarResponseDTO> getSchedule(@RequestBody CalendarRequestDTO calendarRequestDTO, HttpServletRequest request){
        return calendarService.getSchedule(calendarRequestDTO,request);
    }

    @PostMapping
    public String addSchedule (@RequestBody CalendarRequestDTO calendarRequestDTO,HttpServletRequest request){
        return calendarService.addSchedule(calendarRequestDTO,request);
    }

    @PutMapping("/{id}")
    public String updateSchedule (@PathVariable Long id,@RequestBody CalendarRequestDTO calendarRequestDTO, HttpServletRequest request){
        return calendarService.updateSchedule(id,calendarRequestDTO,request);
    }

    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable Long id,HttpServletRequest request){
        return calendarService.deleteSchedule(id, request);
    }
}
