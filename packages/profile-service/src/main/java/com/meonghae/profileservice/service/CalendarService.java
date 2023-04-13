package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.CalendarDTO;
import com.meonghae.profileservice.dto.CalendarPK;
import com.meonghae.profileservice.entity.Calendar;
import com.meonghae.profileservice.entity.CalendarData;
import com.meonghae.profileservice.repository.CalendarDataRepository;
import com.meonghae.profileservice.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final CalendarDataRepository calendarDataRepository;
    private final EntityManager entityManager;

    @Transactional
    // 특정 년, 월에 대한 모든 Calendar와 CalendarData를 가져오는 메서드
    public List<Calendar> getAll(int year,int month){
        // JPQL 쿼리 작성
        // Calendar 엔티티와 연관된 CalendarData를 조인하여 가져오기
        // 연도와 월로 필터링하고, 일자 순으로 정렬하기
        String jpql = "select distinct c from Calendar c left join fetch c.calendarDataList " +
                "where c.year = :year and c.month = :month order by c.day asc";

        // 쿼리 실행 및 결과 반환
        List<Calendar> calendars = entityManager.createQuery(jpql, Calendar.class)
                .setParameter("year", year)
                .setParameter("month", month)
                .getResultList();

        return calendars;
    }
    // CalendarData 추가 메서드
    public String addCalendarData(CalendarDTO calendarDTO) {
        // 해당 날짜의 Calendar 찾기 또는 새로 생성하기
        Calendar calendar = calendarRepository.findById(new CalendarPK(calendarDTO.getYear(),calendarDTO.getMonth(),calendarDTO.getDay()))
                .orElseGet(() -> {
                    Calendar newCalendar = new Calendar().builder()
                            .year(calendarDTO.getYear())
                            .month(calendarDTO.getMonth())
                            .day(calendarDTO.getDay())
                            .calendarDataList(new ArrayList<>())
                            .build();
                    return calendarRepository.save(newCalendar);
                });

        // CalendarData와 Calendar 연결하기
        CalendarData calendarData = new CalendarData().builder()
                .calendar(calendar)
                .email(calendarDTO.getEmail())
                .text(calendarDTO.getText())
                .build();
        // CalendarData를 Calendar에 추가하기
        calendar.getCalendarDataList().add(calendarData);
        CalendarData savedCalendarData = calendarDataRepository.save(calendarData);

        return "일정 저장완료";
    }

}
