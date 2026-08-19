# 📋 [Handover] 대용량 트래픽 대응 게시판형 설문조사 시스템 마스터 아키텍처

> **Author**: Developer (User) & AI Pair Programmer  
> **Target**: TDD 기반 멀티 모듈, CQRS, Redis, Kafka 이벤트 기반 대용량 설문조사 시스템

---

## 1. 비즈니스 요구사항 명세

### 1.1 설문조사 (Survey)
- 설문지 등록, 수정, 삭제, 단건 조회, 목록 조회
- 문항(Question) 및 선택지(Option) 구조 설계
- 설문 참여/응답(Response) 제출
- **조회 성능 최적화**: CQRS 읽기 모델(Read Model) 및 Redis 캐시 적용

### 1.2 계층형 댓글 (Comment)
- 댓글 등록, 수정, 삭제, 단건/목록 조회
- **계층 구조**:
  - 기본: **최대 2-depth (댓글 - 대댓글)**
  - 확장성: **무한 depth** 대응이 가능한 데이터 모델링 (`parent_id`, `top_parent_id`, `depth`, `path`)
- **도메인 격리**: `Survey` 엔티티와 물리적 FK 없이 `survey_id` 식별자 간접 참조 (독립 서버 분리 용이)

### 1.3 설문지 좋아요 (Like)
- 좋아요 토글 (등록/취소)
- 좋아요 총합 집계 및 동시성 제어

### 1.4 설문조사 조회수 (View Count)
- **10분 내 동일 사용자 중복 집계 방지**:
  - Redis Key: `view:survey:{surveyId}:user:{userId}` (TTL: 600초)
  - `SET NX` 성공 시에만 카운팅 이벤트(Kafka) 발행
- **비동기 벌크 반영**: Kafka 컨슈머를 통해 DB 조회수 배치/비동기 업데이트

### 1.5 일간 인기 설문조사 (Popular Surveys)
- **배치 주기**: **매일 01:00 AM**
- **대상**: 일 단위 상위 10건 선정
- **점수 산정 공식**:
  $$\text{Score} = (W_{resp} \times \text{응답수}) + (W_{comment} \times \text{댓글수}) + (W_{like} \times \text{좋아요수}) + (W_{view} \times \text{조회수})$$
- **내역 보관**: `popular_survey_rankings` 테이블에 일자별 스냅샷 저장 (최근 N일간의 인기 설문 랭킹 히스토리 조회 지원)
- **캐싱**: 당일 랭킹 Top 10을 Redis Sorted Set(`ZSET`)에 보관

---

## 2. 멀티 모듈 구조 (Gradle Kotlin DSL)

```
survey/
├── survey-common    : 공통 예외, 표준 응답 DTO, 공통 유틸, Event Message 모델
├── survey-core      : 설문/응답 핵심 도메인 모델, 비즈니스 규칙, JPA 엔티티
├── survey-comment   : 계층형 댓글 도메인 모델, 댓글 비즈니스 로직
├── survey-query     : CQRS 조회 전용(Read Model), QueryDSL DTO 프로젝션, Redis 캐시
├── survey-event     : Kafka Producer/Consumer, 이벤트 기반 비동기 처리
├── survey-batch     : 인기 설문 일간 집계 스케줄러/배치 (매일 01:00 AM)
└── survey-api       : REST Controller, Web Security, Application 진입점
```

---

## 3. 핵심 기술 스택 & 개발 원칙
- **Language / Framework**: Java 17, Spring Boot
- **Build Tool**: Gradle Kotlin DSL (`build.gradle.kts`)
- **Database / Cache**: MySQL 8.x (Master/Replica), Redis
- **Message Broker**: Apache Kafka
- **Architecture**: CQRS, Modular Monolith (Event-Driven), DDD
- **Methodology**: **TDD (Test-Driven Development)**, Clean Code
- **Roles**:
  - **Developer (User)**: 전체 비즈니스 로직 구현 및 TDD 코드 작성
  - **AI Agent**: 아키텍처 설계 검증, TDD 단계별 가이드, 코드 리뷰 및 리팩토링 피드백 제공
