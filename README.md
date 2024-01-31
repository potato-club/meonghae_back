# 🐶 멍해? - 반려동물 수첩 어플리케이션

## 📖 프로젝트 개요 및 구조 설명

### 프로젝트 개요
- 반려동물을 처음 키우는 사람들도 편하고 쉽게 관련 정보를 얻고 관리할 수 있도록 하자는 목표로 개발을 시작하게 됨
- 반려동물에 대한 실종 신고, 소통 등의 부가적인 기능을 활용할 수 있도록 하며, 반려동물에 대한 풍부한 정보에 쉽게 접근할 수 있도록 함
- 사용자들이 쉽게 접근할 수 있는 환경을 위해 앱 개발 선택

### 프로젝트 구성도
> ![1차 배포 구성도](https://github.com/potato-club/meonghae_back/assets/100738591/8988958e-4906-44bc-a8c0-100103b38e75)

### 구글 플레이 스토어 배포
> ![멍해](https://github.com/potato-club/meonghae_back/assets/84797433/f71884bf-0b2f-41c0-8dd2-ba6736a9f370)

## 기술 스택 및 구성도

### 프론트
[프론트엔드 리포지토리 바로가기](https://github.com/potato-club/meonghae_front)
- **Flutter**: 프레임워크
- **Dio**: API 통신 라이브러리
- **Provider**: 전역 상태 관리 라이브러리

### 백엔드
- **Spring Framework**
  - **Spring Boot**: 스프링 기반 프레임워크
  - **Spring Cloud**: MSA 구성 지원 프레임워크
- **Ubuntu + Nginx**: 서버 컴퓨터 환경
- **MySQL**: 기본 데이터베이스
- **AWS RDS**: 클라우드 데이터베이스
- **Firebase (+ RabbitMQ)**: 알림 기능을 위한 데이터베이스
- **Spring Data JPA + QueryDsl**: Type ORM 기술
- **Docker**: 배포를 위한 컨테이너 가상화
- **RabbitMQ (Message Queue)**: 데이터 동기화
- **Jenkins (CI/CD)**: 자동화 배포
- **모니터링**
  - **Prometheus + Grafana**: 통계 시각화 툴

### ERD 구성도
![ERD 구성도](https://github.com/leemj123/meonghae_back/assets/100738591/385e2bf8-826d-43d7-88b2-3ecb69f8b782)

### 각 서비스 역할
- **Eureka Server**
  - MSA 환경에서 각 서비스간의 로드밸런싱 및 각 서비스의 네임을 등록하여 Gateway에서 네이밍으로 접근이 가능하도록 해주는 미들웨어 서버

- **Gateway Service**
  - 각 서비스로의 라우팅 담당 및 보안과 인증을 담당하여 접근에 대한 제어 가능
  - Gateway Filter로 JWT 검증과 잘못된 접근에 대한 Exception 처리를 담당함

- **User Service**
  - 유저가 각 서비스로 접근할 수 있도록 토큰 발급 & 재발급 및 유저 정보를 관리하는 서비스
  - 유저의 정보를 저장 & 수정할 수 있고, 회원탈퇴나 로그아웃과 같은 기능도 여기서 이루어짐
  - Spring Scheduler를 통해 매일 자정마다 탈퇴 후 7일 동안 접속이 없으면 관련 데이터 삭제하는 로직 실행
  - Profile Service의 알림 기능에 필요한 FCM 토큰을 로그인 시점에서 로그아웃 때까지 관리함

- **Community Service**
  - 게시글을 올리거나 애완용품에 대한 리뷰를 작성하는 등 각 유저간 소통을 위한 서비스
  - 게시판은 3가지 타입으로 분류되고, 각 게시판에 대한 CRUD 가능
  - 리뷰는 카탈로그에 따라 생성 & 조회 & 삭제가 가능
  - 커뮤니티는 조회 시 페이징 처리를 통해 무한 스크롤 가능하고, 리뷰는 키워드나 사진 유무에 따른 필터링이 가능
  - 게시글은 좋아요와 댓글 작성 기능이 있고, 리뷰는 추천과 비추천 기능이 있음

- **Profile Service**
  - 유저의 애완동물 정보 및 일정을 관리하는 서비스
  - 펫들의 정보를 저장 & 수정 & 삭제할 수 있음
  - 일정관리를 위한 달력 기능을 제공
  - 일정과 알람은 반복주기를 통해 예방접종 등의 반복되는 스케줄을 자동 일정 설정을 제공
  - 알람은 스케줄러를 통해 RabbitMQ의 delayTime 플러그인으로 설정된 알람시간에 맞춰 Firebase의 Cloud Message(FCM) 기능을 통해 알림을 전송

- **AWS S3 Service**
  - 유저가 업로드하는 모든 종류의 이미지 파일들을 AWS S3 공간에 업로드하고 관리하는 서비스 (프로필 사진, 애완동물 사진, 게시글 사진 등)
  - Feign Client를 통해 거의 모든 서비스들에게 사진 저장 & 삭제 및 다운로드 링크 출력 등의 서비스를 제공함

<br/>

## 🪄 서비스 핵심 기능 요약
### Screen UI

- Kakao 소셜 로그인 구현 (User Service)

  ![image](https://github.com/potato-club/meonghae_back/assets/83578248/8e16ad2e-726c-45da-9f94-f01bdbe9fe97)

- SelectScreen & RegisterScreen
    - 유저 정보는 User Service 에서, 펫 정보는 Profile Service 에서 저장 & 관리함.

      ![image](https://github.com/potato-club/meonghae_back/assets/83578248/b57b5253-df8b-48e4-82d7-7c31fee2ad43)

- VideoPlayerScreen & MainScreen
    - 메인페이지에선 크게 3가지로 나뉨
        - User Service : 내 프로필 정보를 불러옴.
        - Community Service : 각 게시판의 인기글들을 모아 출력함.
        - Profile Service
            - 내가 등록한 펫들의 프로필들을 불러옴.
            - 현재로부터 가장 가까운 순으로 15개의 일정들을 불러옴.

      ![image](https://github.com/potato-club/meonghae_back/assets/83578248/e6c6a9d6-e898-49e5-b044-66a875eadbbe)

- CalendarScreen (Profile Service)
    - 애완동물 관련 일정 등록 및 확인 가능
    - 해당 달의 일정을 아래 일정표에서 순서대로 확인 가능
    - 일정을 반복하고 싶다면 설정 칸에서 원하는 주기로 반복 설정 가능
    - 맞춰놓은 일정이 오면 알림 기능 동작

      ![image](https://github.com/potato-club/meonghae_back/assets/83578248/272c9994-1f41-4206-9839-535296b9eb7b)

- PostScreen (Community Service)
    - 멍자랑 / 웃긴멍 / 실종신고 의 카테고리 별 게시글
    - 각 게시글에는 좋아요와 댓글 작성 가능 (댓글은 대댓글까지 가능함)
    - 본인이 작성한 게시글이나 댓글에 댓글이 달릴 시 알림 기능 동작
    - 게시글은 20개씩 조회되며 무한스크롤로 추가 조회 가능

      ![image](https://github.com/potato-club/meonghae_back/assets/83578248/171daeaf-1562-4e34-aa25-da9cc6bc6ff0)

- ReviewScreen (Community Service)
    - 애완동물 관련 물품 리뷰 작성 및 확인 가능
    - 카탈로그별 리뷰 조회와 작성
    - 리뷰 조회 시 제목에서의 키워드 검색과 사진 필터링 가능
    - 리뷰는 15개씩 조회되며 무한스크롤로 추가 조회 가능

      ![image](https://github.com/potato-club/meonghae_back/assets/83578248/d21d1b86-fa61-4fd2-a226-e3b0cb379289)

- InquiryScreen
    - FAQ(자주하는 질문) / 1:1문의 작성 및 확인 가능

      ![image](https://github.com/potato-club/meonghae_back/assets/83578248/ca97aefa-2aaf-42f6-b7ee-321652ba6271)

<br/>

- **서버 모니터링**
>   - `Prometheus` - 각 서비스의 상태와 동작을 나타내는 지표를 수집해 내부 데이터베이스에 저장하고 PromQL이라는 전용 쿼리 언어를 통해 지표에 대한 데이터를 조회할 수 있음
      >   <img width="1440" alt="image" src="https://github.com/potato-club/meonghae_back/assets/94947782/410db3cc-74b0-4993-b507-de0e29a681fb">

<br/>

>   - `Grafana` - Prometheus와 함께 사용되는 시각화 대시보드 도구로 수집한 지표를 조회한 데이터를 사용자의 요구에 맞게 확인할 수 있음
>
>   ![화면 캡처 2023-06-14 200249](https://github.com/potato-club/meonghae_back/assets/84797433/b8eb1787-ddee-480f-bc83-5d8319e348a4)

<br/>

## 💫 개발 경험에 대한 평가

### **MSA로 개발하면서..**

시스템을 여러 개의 독립적인 서비스로 분리하는 과정 → 각 서비스의 개발, 배포, 관리에 대한 복잡성으로 서버 구축의 난이도가 향상

→ 복잡성을 관리하기 위해, `Docker`와 `Jenkins`를 사용하여 각각의 마이크로 서비스를 컨테이너화하고, 지속적인 통합 및 지속적인 배포를 수행함을 목표로 개발

### **그럼에도 왜 MSA 인가**

- **높은 확장성**

MSA 방식에서는 각각의 서비스가 독립적으로 배포 및 업데이트가 가능 → 시스템 전반에 대한 유연성을 극대화, 개별 서비스에 필요한 변경사항을 신속하게 적용
→ 백엔드 개발 과정에서 유연성이 크게 향상

- **장애 격리의 이점**.

각 서비스가 독립적으로 운영 → 한 서비스에서 발생한 문제가 다른 서비스로 퍼져가는 것을 방지할 수 있음
→ 전체 시스템의 안정성을 보장, 각 서비스를 고립시켜 개별적으로 트러블슈팅을 할 수 있는 환경을 제공

이렇게, `MSA`는 우리 팀이 더 유연하고 안정적인 시스템을 만드는 데 큰 도움을 주었음

<br/>
