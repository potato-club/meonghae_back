package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.calendar.ScheduleRequestDTO;
import com.meonghae.profileservice.dto.calendar.ScheduleResponseDTO;
import com.meonghae.profileservice.entity.QSchedule;
import com.meonghae.profileservice.entity.Schedule;
import com.meonghae.profileservice.entity.Pet;
import com.meonghae.profileservice.entity.QPet;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.ScheduleRepository;
import com.meonghae.profileservice.repository.PetRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ScheduleService {
  private final PetRepository petRepository;

  private final ScheduleRepository scheduleRepository;
  // dsl
  private final JPAQueryFactory jpaQueryFactory;
  private final FeignService feignService;


  // 프로필 화면에서 가까운 일정 순서대로 표시하기위함.
  @Transactional
  public List<ScheduleResponseDTO> getProfileSchedule(String token) {
    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;
    List<Schedule> result =
        jpaQueryFactory
            .select(qSchedule)
            .from(qSchedule)
            .innerJoin(qSchedule.pet, qPet)
            .where(
                    qSchedule
                    .userEmail
                    .eq(userEmail)
                    .and(qSchedule.scheduleTime.after(LocalDate.now().atStartOfDay())))
            .limit(30)
            .orderBy(qSchedule.scheduleTime.asc())
            .fetch();

    return result.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());
  }

  // 달력에서 출력하기 위함 - 날짜 하나 클릭시 그 날짜에 대한 일정들 리턴
  @Transactional
  public List<ScheduleResponseDTO> getSchedule(
          LocalDateTime startOfDate, String token) {

    LocalDateTime endOfDate = startOfDate.plusDays(1).minusNanos(1); // ex) 13일 23시 59분

    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    List<Schedule> result =
        jpaQueryFactory
            .select(qSchedule)
            .from(qSchedule)
            .innerJoin(qSchedule.pet, qPet)
            .where(
                    qSchedule.userEmail.eq(userEmail).and(qSchedule.scheduleTime.between(startOfDate, endOfDate)))
            .orderBy(qSchedule.scheduleTime.asc())
            .fetch();

    return result.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());
  }

  // 달력 월단위 일정들 보기 위한 함수 - 같은 해 같은 월 데이터 출력
  @Transactional
  public List<ScheduleResponseDTO> getMonthSchedule(LocalDate startOfDate, String token) {

    LocalDate endOfDate = startOfDate.plusMonths(1).minusDays(1);

    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    List<Schedule> result =
        jpaQueryFactory
            .select(qSchedule)
            .from(qSchedule)
            .innerJoin(qSchedule.pet, qPet)
            .where(
                    qSchedule
                    .userEmail
                    .eq(userEmail)
                    .and(
                            qSchedule.scheduleTime.between(
                            startOfDate.atStartOfDay(), endOfDate.atStartOfDay())))
            .orderBy(qSchedule.scheduleTime.asc())
            .fetch();
    return result.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());
  }


  @Transactional
  public List<ScheduleResponseDTO> getScheduleOfFindByText(String key, String token){
    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    List<Schedule> scheduleList = jpaQueryFactory
                    .selectFrom(qSchedule)
                    .innerJoin(qSchedule.pet, qPet).fetchJoin()
                    .where(qSchedule.userEmail.eq(userEmail)
                            .and((qSchedule.text.like("%"+key+"%")
                                    .or(qPet.petName.like("%"+key+"%")))))
                    .orderBy(qSchedule.scheduleTime.asc())
                    .fetch();

    return scheduleList.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());
  }

  @Transactional
  public String addSchedule(ScheduleRequestDTO scheduleRequestDTO, String token) {
    Pet pet = petRepository.findById(scheduleRequestDTO.getPetId())
            .orElseThrow(() -> {throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});

    String userEmail = feignService.getUserEmail(token);

    Schedule schedule =
            new Schedule()
                    .builder()
                    .userEmail(userEmail)
                    .pet(pet)
                    .title(scheduleRequestDTO.getTitle())
                    .scheduleType(scheduleRequestDTO.getScheduleType())
                    .scheduleTime(scheduleRequestDTO.getScheduleTime())
                    .alarmTime(scheduleRequestDTO.getAlarmTime())
                    .text(scheduleRequestDTO.getText())
                    .build();
    scheduleRepository.save(schedule);
    return "일정 추가 완료";
  }

  // Update
  @Transactional
  public String updateSchedule(
      Long id, ScheduleRequestDTO scheduleRequestDTO) {
    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    Pet pet =
        petRepository
            .findById(scheduleRequestDTO.getPetId())
            .orElseThrow(
                () -> {
                  throw new NotFoundException(
                      ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());
                });

    Schedule schedule =
        jpaQueryFactory
            .select(qSchedule)
            .from(qSchedule)
            .innerJoin(qSchedule.pet, qPet)
            .where(qSchedule.id.eq(id))
            .fetchOne(); // 단일 건수의 데이터 조회 둘 이상일 경우 NonUniqueResultException

    schedule.update(scheduleRequestDTO, pet);
    scheduleRepository.save(schedule);
    return "수정 완료";
  }

  // Delete
  @Transactional
  public String deleteSchedule(Long id) {

    scheduleRepository.deleteById(id);
    return "삭제 완료";
  }
}
