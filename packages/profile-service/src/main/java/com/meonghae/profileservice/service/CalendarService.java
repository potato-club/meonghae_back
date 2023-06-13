package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.calendar.CalendarRequestDTO;
import com.meonghae.profileservice.dto.calendar.CalendarResponseDTO;
import com.meonghae.profileservice.entity.Calendar;
import com.meonghae.profileservice.entity.Pet;
import com.meonghae.profileservice.entity.QCalendar;
import com.meonghae.profileservice.entity.QPet;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.BadRequestException;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.CalendarRepository;
import com.meonghae.profileservice.repository.PetRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {
  private final PetRepository petRepository;

  private final CalendarRepository calendarRepository;
  // dsl
  private final JPAQueryFactory jpaQueryFactory;
  private final FeignService feignService;

  // 프로필 화면에서 가까운 일정 순서대로 표시하기위함.
  @Transactional
  public List<CalendarResponseDTO> getProfileSchedule(String token) {
    String userEmail = feignService.getUserEmail(token);

    QCalendar qCalendar = QCalendar.calendar;
    QPet qPet = QPet.pet;
    List<Calendar> result =
        jpaQueryFactory
            .select(qCalendar)
            .from(qCalendar)
            .innerJoin(qCalendar.pet, qPet)
            .where(
                qCalendar
                    .userEmail
                    .eq(userEmail)
                    .and(qCalendar.scheduleTime.after(LocalDate.now().atStartOfDay())))
            .limit(30)
            .orderBy(qCalendar.scheduleTime.asc())
            .fetch();

    return result.stream().map(CalendarResponseDTO::new).collect(Collectors.toList());
  }

  // 달력에서 출력하기 위함 - 날짜 하나 클릭시 그 날짜에 대한 일정들 리턴
  @Transactional
  public List<CalendarResponseDTO> getSchedule(
          LocalDateTime startOfDate, String token) {

    LocalDateTime endOfDate = startOfDate.plusDays(1).minusNanos(1); // ex) 13일 23시 59분

    String userEmail = feignService.getUserEmail(token);

    QCalendar qCalendar = QCalendar.calendar;
    QPet qPet = QPet.pet;

    List<Calendar> result =
        jpaQueryFactory
            .select(qCalendar)
            .from(qCalendar)
            .innerJoin(qCalendar.pet, qPet)
            .where(
                qCalendar.userEmail.eq(userEmail).and(qCalendar.scheduleTime.between(startOfDate, endOfDate)))
            .orderBy(qCalendar.scheduleTime.asc())
            .fetch();

    return result.stream().map(CalendarResponseDTO::new).collect(Collectors.toList());
  }

  // 달력 월단위 일정들 보기 위한 함수 - 같은 해 같은 월 데이터 출력
  @Transactional
  public List<CalendarResponseDTO> getMonthSchedule(LocalDate startOfDate, String token) {

    LocalDate endOfDate = startOfDate.plusMonths(1).minusDays(1);

    String userEmail = feignService.getUserEmail(token);

    QCalendar qCalendar = QCalendar.calendar;
    QPet qPet = QPet.pet;

    List<Calendar> result =
        jpaQueryFactory
            .select(qCalendar)
            .from(qCalendar)
            .innerJoin(qCalendar.pet, qPet)
            .where(
                qCalendar
                    .userEmail
                    .eq(userEmail)
                    .and(
                        qCalendar.scheduleTime.between(
                            startOfDate.atStartOfDay(), endOfDate.atStartOfDay())))
            .orderBy(qCalendar.scheduleTime.asc())
            .fetch();
    return result.stream().map(CalendarResponseDTO::new).collect(Collectors.toList());
  }

  @Transactional
  public List<CalendarResponseDTO> getScheduleOfFindByText(String key){
    QCalendar qCalendar = QCalendar.calendar;
    QPet qPet = QPet.pet;

    List<Calendar> calendarList =
            jpaQueryFactory
                    .selectFrom(qCalendar)
                    .join(qCalendar.pet, qPet)
                    .where(qCalendar.text.like("%"+key+"%")
                            .or(qCalendar.pet.petName.like("%"+key+"%")))
                    .fetch();

    return calendarList.stream().map(CalendarResponseDTO::new).collect(Collectors.toList());
  }

  @Transactional
  public String addSchedule(CalendarRequestDTO calendarRequestDTO, String token) {
    // 프론트에서 FM 이면 hour에 +12해서 주겠징
    LocalDateTime scheduleDateTime = calendarRequestDTO.getScheduleTime();

    Pet pet = petRepository.findById(calendarRequestDTO.getPetId())
            .orElseThrow(() -> {throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});

    String userEmail = feignService.getUserEmail(token);

    Calendar calendar =
        new Calendar()
            .builder()
            .userEmail(userEmail)
            .pet(pet)
            .scheduleTime(scheduleDateTime)
            .text(calendarRequestDTO.getText())
            .build();
    calendarRepository.save(calendar);
    return "일정 추가 완료";
  }

  // Update
  @Transactional
  public String updateSchedule(
      Long id, CalendarRequestDTO calendarRequestDTO) {
    QCalendar qCalendar = QCalendar.calendar;
    QPet qPet = QPet.pet;

    Pet pet =
        petRepository
            .findById(calendarRequestDTO.getPetId())
            .orElseThrow(
                () -> {
                  throw new NotFoundException(
                      ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());
                });

    Calendar calendar =
        jpaQueryFactory
            .select(qCalendar)
            .from(qCalendar)
            .innerJoin(qCalendar.pet, qPet)
            .where(qCalendar.id.eq(id))
            .fetchOne(); // 단일 건수의 데이터 조회 둘 이상일 경우 NonUniqueResultException

    calendar.update(calendarRequestDTO, pet);
    calendarRepository.save(calendar);
    return "수정 완료";
  }

  // Delete
  @Transactional
  public String deleteSchedule(Long id) {

    calendarRepository.deleteById(id);
    return "삭제 완료";
  }
}
