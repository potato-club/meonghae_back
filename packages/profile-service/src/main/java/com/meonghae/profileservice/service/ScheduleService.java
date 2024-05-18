package com.meonghae.profileservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meonghae.profileservice.dto.schedule.*;
import com.meonghae.profileservice.entity.*;
import com.meonghae.profileservice.enumCustom.ScheduleCycleType;
import com.meonghae.profileservice.enumCustom.ScheduleType;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.ScheduleRepository;
import com.meonghae.profileservice.repository.PetRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
  private final JPAQueryFactory jpaQueryFactory;
  private final FeignService feignService;
  private final RabbitService rabbitService;


  public String fcmTest(ScheduleRequestDTO scheduleRequestDTO, String token) {
    String userEmail = feignService.getUserEmail(token);
    AlarmDto alarmDto = new AlarmDto(scheduleRequestDTO,userEmail);
    rabbitService.sendToRabbitMq(alarmDto);
    return "알람시간은 어느정도 여유를 두고 해주세요. 요청시 hasRepeat, text, ScheduleType은 Test로 필수 요소임";
  }
  @Transactional
  public ScheduleResponseDTO getSchedule(Long id, String token) {
    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    Schedule schedule = jpaQueryFactory
            .selectFrom(qSchedule)
            .leftJoin(qSchedule.pet,qPet)
            .where(qSchedule.userEmail.eq(userEmail)
                    .and(qSchedule.id.eq(id)))
            .fetchOne();

    return new ScheduleResponseDTO(schedule);
  }

  // 프로필 화면에서 가까운 일정 순서대로 표시하기위함.
  @Transactional
  public List<SchedulePreviewResponseDto> getProfileSchedule(String token) {
    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    List<Schedule> scheduleList = jpaQueryFactory
            .selectFrom(qSchedule)
            .leftJoin(qSchedule.pet,qPet)
            .where(qSchedule.userEmail.eq(userEmail)
                    .and(qSchedule.scheduleEndTime.goe(LocalDateTime.now())
                            .or(qSchedule.hasRepeat.isFalse().and(qSchedule.scheduleTime.goe(LocalDateTime.now())))))
            .fetch();

    List<SchedulePreviewResponseDto> resultList = new ArrayList<>();

    // 반복 일정을 가장 가까운 미래 일정으로 변환하여 allSchedules에 추가
    for ( Schedule schedule : scheduleList ) {
      if (!schedule.isHasRepeat()) {
        resultList.add(new SchedulePreviewResponseDto(schedule));
      }
      else {
        ChronoUnit unit = (schedule.getCycleType() == ScheduleCycleType.Month) ? ChronoUnit.MONTHS : ChronoUnit.DAYS;
        processRepeatSchedule(resultList, schedule, unit);
      }
    }
    // 결과 리스트를 최신 날짜 순으로 정렬
    resultList.sort(Comparator.comparing(SchedulePreviewResponseDto::getScheduleDate));

    // 상위 5개만 리턴
    return resultList.size() > 5 ? resultList.subList(0, 5) : resultList;
  }


  //날짜 하나 클릭시 그 날짜에 대한 일정들 리턴
  @Transactional
  public List<ScheduleResponseDTO> getDayOfSchedule(
          LocalDate targetDate, String token, List<Long> scheduleId) {

    String userEmail = feignService.getUserEmail(token);
    QSchedule qSchedule = QSchedule.schedule;

    List<Schedule> scheduleList = jpaQueryFactory
            .selectFrom(qSchedule)
            .where(qSchedule.id.in(scheduleId).and(qSchedule.userEmail.eq(userEmail)))
            .fetch();

    List<ScheduleResponseDTO> resultList = new ArrayList<>();

    for (Schedule schedule : scheduleList) {
      resultList.add(new ScheduleResponseDTO(schedule,LocalDateTime.of(targetDate.getYear(),targetDate.getMonth(),schedule.getScheduleTime().getDayOfMonth(),schedule.getScheduleTime().getHour(),schedule.getScheduleTime().getMinute())));
    }
    return resultList;
  }

  // 달력 월단위 일정들 보기 위한 함수 - 같은 해 같은 월 데이터 출력
  @Transactional
  public List<SimpleMonthSchedule> getMonthOfSchedule(LocalDate targetDate, String token) throws JsonProcessingException {
  //3달치 리턴하기!!

    String userEmail = feignService.getUserEmail(token);
    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;
    LocalDateTime monthStartPoint = targetDate.atStartOfDay().minusMonths(1);
    LocalDateTime monthEndPoint = targetDate.atStartOfDay().plusMonths(2) .minusDays(1);

    //반복이 끝나는 시점이 탐색 시작 시간 보다는 커야하며 && 반복이 시작되는 시점이 탐색 끝 시간보다는 작아야한다.
    List<Schedule> scheduleList = jpaQueryFactory
            .selectFrom(qSchedule)
            .leftJoin(qSchedule.pet,qPet)
            .where(qSchedule.userEmail.eq(userEmail)
                    .and(
                            qSchedule.hasRepeat.isTrue()
                                    .and(qSchedule.scheduleEndTime.goe(monthStartPoint)
                                            .and(qSchedule.scheduleTime.loe(monthEndPoint)))
                                    .or(
                                            qSchedule.hasRepeat.isFalse()
                                                    .and(qSchedule.scheduleTime.between(monthStartPoint,monthEndPoint))
                                    )
                    ))
            .fetch();

    //return list에 3달치 설정해두고..
    Map<Integer, List<SimpleSchedule>> monthToSchedulesMap = new HashMap<>();
    monthToSchedulesMap.put(targetDate.minusMonths(1).getMonthValue(),new ArrayList<>());
    monthToSchedulesMap.put(targetDate.getMonthValue(),new ArrayList<>());
    monthToSchedulesMap.put(targetDate.plusMonths(1).getMonthValue(),new ArrayList<>());


    for (Schedule schedule : scheduleList) {
      //일반 일정은 시작지점은 골랐으니 달 비교로 해당하는거만 넣고 3달치 뽑기
      if (!schedule.isHasRepeat() && (schedule.getScheduleTime().getMonthValue() == targetDate.getMonthValue()
              || schedule.getScheduleTime().getMonthValue() == targetDate.minusMonths(1).getMonthValue()
              || schedule.getScheduleTime().getMonthValue() == targetDate.plusMonths(1).getMonthValue())) {

        addSimpleSchedule(monthToSchedulesMap, schedule.getScheduleTime(), schedule.getId().intValue());

      }
      //반복되는 생일이나 예방접종인 경우
      else if (schedule.isHasRepeat() && !schedule.getScheduleType().equals(ScheduleType.Custom)) {
        //1로해서 월단위만 비교하게
        LocalDate scheduleStartPoint = LocalDate.of(schedule.getScheduleTime().getYear(),schedule.getScheduleTime().getMonth(),1);
        LocalDate requestedTargetPoint = LocalDate.of(targetDate.getYear(), targetDate.getMonth(),1);

        //3개달 구해야하니 -1 부터 시작
        for (int i = -1; i<= 1; i++) {
          LocalDate checkMonth = requestedTargetPoint.plusMonths(i);
          if (checkMonth.isBefore(requestedTargetPoint)) continue;
          if(isEventScheduled(scheduleStartPoint, schedule.getScheduleType().getRepeatCycle(), checkMonth)){
            addSimpleSchedule(monthToSchedulesMap, checkMonth.withDayOfMonth(schedule.getScheduleTime().getDayOfMonth()).atStartOfDay(), schedule.getId());
          }
        }
      }
      //주기타입이 month 인 것
      else if (schedule.isHasRepeat() && schedule.getCycleType().equals(ScheduleCycleType.Month)) {

        for (int i = 0; i < 3; i++) { // 현재 달부터 2달 뒤까지 3번 반복
          LocalDate repeatedDate = targetDate.plusMonths(i);
          if ((repeatedDate.getMonthValue() - schedule.getScheduleTime().getMonthValue()) % schedule.getCycle() == 0) {
            addSimpleSchedule(monthToSchedulesMap, repeatedDate.withDayOfMonth(schedule.getScheduleTime().getDayOfMonth()).atStartOfDay(), schedule.getId());
          }
        }
      }
      //반복 일정 && 타입이 커스텀이면서 주기타입이 day 인 것
      else if (schedule.isHasRepeat()
              && schedule.getCycleType() == ScheduleCycleType.Day) {
        List<LocalDateTime> intendedTimeList = calculateRepeatedDays(schedule,targetDate);

        for (LocalDateTime intendedTime : intendedTimeList) {
          addSimpleSchedule(monthToSchedulesMap, intendedTime, schedule.getId());
        }
      } else throw new RuntimeException();
    }
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(monthToSchedulesMap);
    log.info(json);
    List<SimpleMonthSchedule> result = new ArrayList<>();
    for (Map.Entry<Integer, List<SimpleSchedule>> entry : monthToSchedulesMap.entrySet()) {
      SimpleMonthSchedule monthSchedule = new SimpleMonthSchedule();
      monthSchedule.setMonth(entry.getKey());
      monthSchedule.setSchedules(entry.getValue());
      result.add(monthSchedule);
    }

    return result;

  }

  public static boolean isEventScheduled(LocalDate eventStartMonth, int eventCycle, LocalDate requestedMonth) {
    long monthsBetween = ChronoUnit.MONTHS.between(eventStartMonth, requestedMonth);
    return monthsBetween % eventCycle == 0;
  }

  public List<ScheduleResponseDTO> getScheduleOfFindByText(String key, String token){
    String userEmail = feignService.getUserEmail(token);

    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

//검색 키워드를 포함하는 ScheduleType
    List<ScheduleType> matchingKeys = Arrays.stream(ScheduleType.values())
            .filter(type -> type.getTitle().contains(key))
            .collect(Collectors.toList());

    BooleanExpression condition = null;
    if (!matchingKeys.isEmpty() ) {
      condition = qSchedule.scheduleType.in(matchingKeys);
    }

    List<Schedule> scheduleList = jpaQueryFactory
            .selectFrom(qSchedule)
            .innerJoin(qSchedule.pet, qPet).fetchJoin()
            .where(qSchedule.userEmail.eq(userEmail)
                    .and((qSchedule.text.like("%"+key+"%")
                            .or(qSchedule.customScheduleTitle.like("%"+key+"%"))
                            .or(condition)
                            .or(qPet.petName.like("%"+key+"%"))
                    )))
            .orderBy(qSchedule.scheduleTime.asc())
            .fetch();

    return scheduleList.stream().map(ScheduleResponseDTO::new).collect(Collectors.toList());
  }

  @Transactional
  public String addSchedule(ScheduleRequestDTO scheduleRequestDTO, String token) {
    Pet pet = petRepository.findById(scheduleRequestDTO.getPetId())
            .orElseThrow(() -> {throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});

    String userEmail = feignService.getUserEmail(token);
    LocalDateTime repeatEndTime = LocalDateTime.of(1,1,1,1,0);
    //반복하니? // 반복 안되는 것들은 다 위 기본값으로 들어감
    if (scheduleRequestDTO.isHasRepeat()) {
      //일정 타입비교
      if (scheduleRequestDTO.getScheduleType().equals(ScheduleType.Custom)) {
        if (scheduleRequestDTO.getText() == null)
          scheduleRequestDTO.setText(scheduleRequestDTO.getCustomScheduleTitle());
        switch (scheduleRequestDTO.getCycleType().getKey()) {
          //0이 달 1이 일 // 무한 반복설정 or 반복주기 * 반복횟수
          case 0 : repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,1,1,0,0)
                  : scheduleRequestDTO.getScheduleTime().plusMonths((long) scheduleRequestDTO.getCycle() * scheduleRequestDTO.getCycleCount());
          break;
          case 1 : repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,1,1,0,0)
                  : scheduleRequestDTO.getScheduleTime().plusDays((long) scheduleRequestDTO.getCycle() * scheduleRequestDTO.getCycleCount());
          break;
        }

      } else {// 무한 반복설정 or 반복주기 * 반복횟수
        repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,1,1,0,0)
                : scheduleRequestDTO.getScheduleTime().plusMonths((long) scheduleRequestDTO.getScheduleType().getRepeatCycle() * scheduleRequestDTO.getCycleCount());
        scheduleRequestDTO.setCycleType(ScheduleCycleType.Month);
        scheduleRequestDTO.setCycle(scheduleRequestDTO.getScheduleType().getRepeatCycle());
      }
    }


    Schedule schedule = new Schedule(pet,userEmail,repeatEndTime,scheduleRequestDTO);
    scheduleRepository.save(schedule);
    return "일정 추가 완료";
  }

  // Update
  @Transactional
  public String updateSchedule(
      Long id, ScheduleRequestDTO scheduleRequestDTO) {
    QSchedule qSchedule = QSchedule.schedule;
    QPet qPet = QPet.pet;

    Pet pet = petRepository.findById(scheduleRequestDTO.getPetId()).orElseThrow(() -> {throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});

    Schedule schedule =
        jpaQueryFactory
            .select(qSchedule)
            .from(qSchedule)
            .innerJoin(qSchedule.pet, qPet)
            .where(qSchedule.id.eq(id))
            .fetchOne(); // 단일 건수의 데이터 조회 둘 이상일 경우 NonUniqueResultException

    LocalDateTime repeatEndTime = LocalDateTime.of(1,1,1,1,0);
    //반복하니? // 반복 안되는 것들은 다 위 기본값으로 들어감
    if (scheduleRequestDTO.isHasRepeat()) {
      //일정 타입비교
      if (scheduleRequestDTO.getScheduleType().equals(ScheduleType.Custom)) {
        if (scheduleRequestDTO.getText() == null)
          scheduleRequestDTO.setText(scheduleRequestDTO.getCustomScheduleTitle());
        switch (scheduleRequestDTO.getCycleType().getKey()) {
          //0이 달 1이 일 // 무한 반복설정 or 반복주기 * 반복횟수
          case 0 : repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,1,1,0,0)
                  : scheduleRequestDTO.getScheduleTime().plusMonths((long) scheduleRequestDTO.getCycle() * scheduleRequestDTO.getCycleCount());
            break;
          case 1 : repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,1,1,0,0)
                  : scheduleRequestDTO.getScheduleTime().plusDays((long) scheduleRequestDTO.getCycle() * scheduleRequestDTO.getCycleCount());
            break;
        }

      } else {// 무한 반복설정 or 반복주기 * 반복횟수
        repeatEndTime = scheduleRequestDTO.getCycleCount() == 0 ? LocalDateTime.of(2100,1,1,0,0)
                : scheduleRequestDTO.getScheduleTime().plusMonths((long) scheduleRequestDTO.getScheduleType().getRepeatCycle() * scheduleRequestDTO.getCycleCount());
        scheduleRequestDTO.setCycleType(ScheduleCycleType.Month);
        scheduleRequestDTO.setCycle(scheduleRequestDTO.getScheduleType().getRepeatCycle());
      }
    }
    schedule.update(scheduleRequestDTO, pet, repeatEndTime);
    return "수정 완료";
  }

  // Delete
  @Transactional
  public String deleteSchedule(Long id) {

    scheduleRepository.deleteById(id);
    return "삭제 완료";
  }

 //=========================================================

  private void addSimpleSchedule(Map<Integer, List<SimpleSchedule>> monthToSchedulesMap, LocalDateTime date, long scheduleId) {
    //해당 달이 있는지 부터 체크하고, 없으면 체크할 범위 아님

    List<SimpleSchedule> scheduleList = monthToSchedulesMap.get(date.getMonthValue());
    if (scheduleList == null) {
      return;
    }

    //해당 일이 존재하는지 찾고 있으면 id 추가,
    Optional<SimpleSchedule> existingSchedule = scheduleList.stream().filter(s -> s.getDay() == date.getDayOfMonth()).findFirst();
    if (existingSchedule.isPresent()) {
      existingSchedule.get().getScheduleIds().add(scheduleId);
    }
    else {//없으면 날짜 생성
      SimpleSchedule simpleSchedule = new SimpleSchedule(date.getDayOfMonth(),scheduleId);
      scheduleList.add(simpleSchedule);
    }
  }

  // 날짜 관련 유틸리티 메서드
  private List<LocalDateTime> calculateRepeatedDays(Schedule schedule, LocalDate targetDate) {
    List<LocalDateTime> recurringDates = new ArrayList<>();

    LocalDateTime nextScheduleTime = schedule.getScheduleTime();
    // 목표 날짜와 시작 날짜 사이의 차이 계산 / 필요한 반복 횟수 계산
    long repeatCount = (ChronoUnit.DAYS.between(nextScheduleTime.toLocalDate(), targetDate)) / schedule.getCycle();

    nextScheduleTime = nextScheduleTime.plusDays(repeatCount * schedule.getCycle());

    // 3개의 월의 마지막 날짜까지 반복
    while (nextScheduleTime.toLocalDate().isBefore(targetDate.plusMonths(3))) {
      if (nextScheduleTime.getMonthValue() == targetDate.getMonthValue()
              || nextScheduleTime.getMonthValue() == targetDate.plusMonths(1).getMonthValue()
              || nextScheduleTime.getMonthValue() == targetDate.plusMonths(2).getMonthValue()) {
        recurringDates.add(nextScheduleTime);
      }
      nextScheduleTime = nextScheduleTime.plusDays(schedule.getCycle());
    }

    return recurringDates;
  }

  private void processRepeatSchedule(List<SchedulePreviewResponseDto> resultList, Schedule schedule, ChronoUnit unit) {
    LocalDateTime nextScheduleTime = schedule.getScheduleTime();
    //몇달을 추가해서 일정에 보여줘야하는지
    int repeatCount = (int) (unit.between(nextScheduleTime, LocalDateTime.now()) / schedule.getCycle());
    //같은 달에 있는 일정인데 day가 이미 지났니? 지난거면 다음 반복주기로 1, 아님 0
    if (repeatCount == 0) {
      repeatCount = nextScheduleTime.isBefore(LocalDateTime.now()) ? 1 : 0;
    }
    nextScheduleTime = nextScheduleTime.plus((long) repeatCount * schedule.getCycle(), unit);

    if (nextScheduleTime.isBefore(LocalDateTime.now()))
      nextScheduleTime = nextScheduleTime.plus(schedule.getCycle(),unit);

    for (int i = 0; i < 5; i++) {
      if (schedule.getScheduleEndTime().isBefore(nextScheduleTime)) {
        break;
      }
      if (resultList.size() >= 5) {
        // resultList를 정렬하여 가장 늦은 일정을 확인
        resultList.sort(Comparator.comparing(SchedulePreviewResponseDto::getScheduleDate));
        // 가장 늦은 일정보다 현재 추가할 일정이 빠른 경우
        if (nextScheduleTime.isBefore(resultList.get(4).getScheduleDate())) {
          // 가장 늦은 일정을 제거하고 새 일정 추가
          resultList.remove(4);
          resultList.add(new SchedulePreviewResponseDto(schedule, nextScheduleTime));
        } else break;
      } else {
        resultList.add(new SchedulePreviewResponseDto(schedule, nextScheduleTime));
      }
      nextScheduleTime = nextScheduleTime.plus(schedule.getCycle(), unit);
    }
  }
}
