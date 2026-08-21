# 예외 처리 컨벤션 (Exception Specification)

## 1. 설계 원칙 및 목표
- **ErrorCode 중심 설계**: 모든 비즈니스 예외는 독립된 Exception 클래스를 과도하게 생성하지 않고, `ErrorCode` 인터페이스를 구현한 Enum을 통해 일관되게 관리한다.
- **계층별 역할 분리**:
  - `survey-common`: 공통 인터페이스(`ErrorCode`), 최상위 비즈니스 예외(`BusinessException`), 공통 에러 코드(`CommonErrorCode`)
  - `survey-core`: 도메인 전용 에러 코드(`SurveyErrorCode`, `ResponseErrorCode` 등)
  - `survey-api`: 전역 예외 핸들러(`GlobalExceptionHandler`), 클라이언트 응답 DTO(`ErrorResponse`)
- **표준화된 에러 응답**: 클라이언트에는 항상 일관된 포맷의 JSON 에러 응답을 반환한다.

---

## 2. 모듈별 구성 및 패키지 구조

```
[ survey-common ]
  └── src/main/java/com/kuke/survey/common/exception/
        ├── ErrorCode.java             # 에러 코드 인터페이스
        ├── CommonErrorCode.java       # 전역/서버 공통 에러 코드 Enum
        └── BusinessException.java     # 최상위 비즈니스 런타임 예외

[ survey-core ]
  └── src/main/java/com/kuke/survey/core/exception/
        ├── SurveyErrorCode.java       # 설문 도메인 에러 코드 Enum
        └── ResponseErrorCode.java     # 설문 응답 도메인 에러 코드 Enum

[ survey-api ]
  └── src/main/java/com/kuke/survey/api/common/exception/
        ├── GlobalExceptionHandler.java # @RestControllerAdvice 전역 예외 처리기
        └── ErrorResponse.java          # 클라이언트 반환용 DTO
```

---

## 3. 핵심 인터페이스 및 클래스 명세

### 3.1. `ErrorCode` (인터페이스) - `survey-common`
```java
package com.kuke.survey.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getHttpStatus(); // HTTP 상태 코드 (400, 404, 409 등)
    String getCode();           // 비즈니스 에러 식별 코드 (예: SURVEY_400_001)
    String getMessage();        // 기본 에러 메시지
}
```

### 3.2. `BusinessException` (클래스) - `survey-common`
```java
package com.kuke.survey.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}
```

---

## 4. 에러 코드 네이밍 및 코드 부여 규칙

### 4.1. 코드 포맷
`[도메인]_[HTTP상태코드]_[순번]` 형태를 따른다.

* **도메인 접두사**:
  * `COMMON`: 공통 / 인프라 / 시스템 에러
  * `SURVEY`: 설문조사 양식 도메인
  * `RESPONSE`: 설문 응답 제출 도메인
* **순번**: `001`, `002`, `003`... 3자리 숫자

### 4.2. `SurveyErrorCode` 정의 예시 - `survey-core`
```java
package com.kuke.survey.core.exception;

import com.kuke.survey.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SurveyErrorCode implements ErrorCode {

    // 400 BAD_REQUEST: 잘못된 입력/제약조건 위반
    INVALID_SURVEY_TITLE(HttpStatus.BAD_REQUEST, "SURVEY_400_001", "설문 제목은 필수이며 공백일 수 없습니다."),
    SURVEY_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "SURVEY_400_002", "설문 제목은 최대 100자까지 가능합니다."),
    INVALID_QUESTION_TITLE(HttpStatus.BAD_REQUEST, "SURVEY_400_003", "질문 제목은 필수이며 공백일 수 없습니다."),
    QUESTION_COUNT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "SURVEY_400_004", "질문은 최대 10개까지만 등록할 수 있습니다."),
    INVALID_OPTION_COUNT(HttpStatus.BAD_REQUEST, "SURVEY_400_005", "선택형 질문은 최소 2개, 최대 10개의 보기가 필요합니다."),
    OPTION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "SURVEY_400_006", "주관식 문항에는 보기를 추가할 수 없습니다."),
    DUPLICATE_OPTION_TEXT(HttpStatus.BAD_REQUEST, "SURVEY_400_007", "동일한 보기 내용이 중복 등록되었습니다."),

    // 404 NOT_FOUND: 리소스 미존재
    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "SURVEY_404_001", "해당 설문조사를 찾을 수 없습니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "SURVEY_404_002", "해당 질문을 찾을 수 없습니다."),

    // 409 CONFLICT: 도메인 상태 충돌
    SURVEY_ALREADY_PUBLISHED(HttpStatus.CONFLICT, "SURVEY_409_001", "이미 배포된 설문조사는 수정하거나 삭제할 수 없습니다."),
    SURVEY_ALREADY_CLOSED(HttpStatus.CONFLICT, "SURVEY_409_002", "이미 종료된 설문조사입니다."),
    CANNOT_PUBLISH_EMPTY_SURVEY(HttpStatus.CONFLICT, "SURVEY_409_003", "질문이 최소 1개 이상 등록되어야 배포할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
```

---

## 5. 클라이언트 응답 규격 (API Response)

### 5.1. JSON 응답 포맷 (`ErrorResponse`)
```json
{
  "timestamp": "2026-08-21T19:50:00.123",
  "status": 409,
  "code": "SURVEY_409_001",
  "message": "이미 배포된 설문조사는 수정하거나 삭제할 수 없습니다."
}
```

### 5.2. `GlobalExceptionHandler` 처리 전략 (`survey-api`)
1. **`BusinessException`**: 정의된 `ErrorCode`의 `HttpStatus`, `code`, `message`를 추출하여 `ErrorResponse`로 반환.
2. **`MethodArgumentNotValidException` (Bean Validation)**: `400 BAD_REQUEST`로 매핑하고 필드별 오류 메시지 포함.
3. **기타 미처리 `Exception`**: `500 INTERNAL_SERVER_ERROR`, `COMMON_500_001`로 로깅 및 공통 에러 반환.
