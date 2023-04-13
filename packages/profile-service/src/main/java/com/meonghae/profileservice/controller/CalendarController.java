package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.CalendarDTO;
import com.meonghae.profileservice.entity.Calendar;
import com.meonghae.profileservice.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping()
    // 특정 년, 월에 대한 모든 Calendar와 CalendarData 가져오기
    public List<Calendar> getAll(@RequestParam("year")int year,@RequestParam("month")int month){
        return calendarService.getAll(year,month);
    }

    @PostMapping()
    public String addCalendarData(@RequestBody CalendarDTO calendarDTO){
        return calendarService.addCalendarData(calendarDTO);
    }
}
