# Survey 도메인 설계 

## 1. Survey (설문조사)
- **속성 및 제약**:
  - 설문 제목(`title`): 필수값, 공백 불가, 최대 100자
  - 설문 설명(`description`): 선택값, 최대 1,000자
  - 설문 상태(`status`): `DRAFT`(작성중), `PUBLISHED`(배포됨), `CLOSED`(종료됨)
- **비즈니스 규칙**:
  - 생성 시 초기 상태는 `DRAFT`이다.
  - `DRAFT` 상태에서만 정보 수정 및 질문 추가/수정/삭제가 가능하다.
  - 질문이 1~10개 존재할 때만 `PUBLISHED`로 배포할 수 있다.
  - `PUBLISHED` 상태에서만 `CLOSED`로 종료할 수 있다.


## 2. SurveyQuestion (질문)
- **속성 및 제약**:
  - 질문 제목(`title`): 필수값, 공백 불가, 최대 200자
  - 질문 설명(`description`): 선택값, 최대 500자
  - 질문 형태(`inputType`):
    - 주관식: `SHORT_ANSWER` (응답 최대 50자), `LONG_ANSWER` (응답 최대 500자)
    - 선택형: `SINGLE_CHOICE` (단일 선택), `MULTIPLE_CHOICE` (다중 선택)
  - 필수 여부(`isRequired`): boolean
  - 노출 순서(`orderNo`): 1부터 순차 증가
- **비즈니스 규칙**:
  - 하나의 설문당 질문은 최소 1개 ~ 최대 10개까지 등록 가능하다.
  - 주관식 문항은 옵션(Option)을 가질 수 없다.
  - 선택형 문항은 최소 2개 ~ 최대 10개의 옵션을 반드시 가져야 한다.

## 3. SurveyQuestionOption (선택형 옵션/보기)
- **속성 및 제약**:
    - 옵션 내용(`optionText`): 필수값, 공백 불가, 최대 20자
    - 노출 순서(`orderNo`): 1부터 순차 증가
- **비즈니스 규칙**:
    - 동일한 질문 내에서 중복된 옵션 텍스트는 등록할 수 없다.

