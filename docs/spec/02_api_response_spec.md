# API 공통 응답 규격 (API Response Specification)

## 1. 설계 원칙 및 목표
- **일관된 응답 구조**: 모든 API 응답(성공 및 실패)은 동일한 최상위 JSON 포맷을 유지하여 클라이언트가 예측 가능한 처리를 할 수 있도록 한다.
- **성공/실패 플래그 분리**: `success` 불리언 필드를 최상단에 두어 클라이언트에서 성공 여부를 즉시 분기할 수 있게 한다.
- **제네릭 활용**: `ApiResponse<T>` 제네릭 클래스로 데이터 타입의 유연성과 컴파일 타임 타입 안정성을 확보한다.
- **위치**: `survey-common` 모듈에 배치하여 모든 모듈과 API 계층에서 공통으로 사용한다.

---

## 2. 표준 JSON 응답 포맷

### 2.1. 성공 응답 (Success)
성공 시 `data` 필드에 실제 결과 객체가 담기며, `error` 필드는 `null`로 내려갑니다.

```json
{
  "success": true,
  "status": 200,
  "data": {
    "surveyId": 123456789,
    "title": "2026 서비스 만족도 조사",
    "status": "DRAFT"
  },
  "message": "설문조사가 성공적으로 생성되었습니다.",
  "timestamp": "2026-08-21T20:50:00.123"
}
```

### 2.2. 실패 응답 (Error / Business Exception)
실패 시 `data`는 `null`이 되며, `error` 필드에 에러 코드와 상세 메시지가 담깁니다.

```json
{
  "success": false,
  "status": 409,
  "data": null,
  "error": {
    "code": "SURVEY_409_001",
    "message": "이미 배포된 설문조사는 수정할 수 없습니다."
  },
  "timestamp": "2026-08-21T20:50:00.123"
}
```

### 2.3. 입력값 검증 실패 응답 (Validation Error)
`@Valid` 유효성 검증 실패 시 어떤 필드에서 문제가 발생했는지 `fieldErrors` 배열을 함께 제공합니다.

```json
{
  "success": false,
  "status": 400,
  "data": null,
  "error": {
    "code": "COMMON_400_001",
    "message": "유효하지 않은 입력값입니다.",
    "fieldErrors": [
      {
        "field": "title",
        "rejectedValue": "",
        "reason": "설문 제목은 필수이며 공백일 수 없습니다."
      }
    ]
  },
  "timestamp": "2026-08-21T20:50:00.123"
}
```

---

## 3. Java 클래스 설계 (`survey-common`)

### 3.1. `ApiResponse<T>`
```java
package com.kuke.survey.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kuke.survey.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final int status;
    private final T data;
    private final String message;
    private final ErrorDetail error;
    private final LocalDateTime timestamp;

    // 📌 성공 팩토리 메서드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, data, null, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(int status, T data, String message) {
        return new ApiResponse<>(true, status, data, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, 201, data, null, null, LocalDateTime.now());
    }

    // 📌 실패 팩토리 메서드 (비즈니스 예외)
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(
                false,
                errorCode.getStatus(),
                null,
                null,
                ErrorDetail.of(errorCode.getCode(), errorCode.getMessage()),
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        return new ApiResponse<>(
                false,
                errorCode.getStatus(),
                null,
                null,
                ErrorDetail.of(errorCode.getCode(), customMessage),
                LocalDateTime.now()
        );
    }

    // 📌 실패 팩토리 메서드 (입력값 유효성 검증 예외)
    public static <T> ApiResponse<T> error(ErrorCode errorCode, List<FieldErrorDetail> fieldErrors) {
        return new ApiResponse<>(
                false,
                errorCode.getStatus(),
                null,
                null,
                ErrorDetail.of(errorCode.getCode(), errorCode.getMessage(), fieldErrors),
                LocalDateTime.now()
        );
    }
}
```

### 3.2. `ErrorDetail` & `FieldErrorDetail`
```java
package com.kuke.survey.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {

    private final String code;
    private final String message;
    private final List<FieldErrorDetail> fieldErrors;

    public static ErrorDetail of(String code, String message) {
        return new ErrorDetail(code, message, null);
    }

    public static ErrorDetail of(String code, String message, List<FieldErrorDetail> fieldErrors) {
        return new ErrorDetail(code, message, fieldErrors);
    }
}
```

```java
package com.kuke.survey.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorDetail {
    private final String field;
    private final Object rejectedValue;
    private final String reason;
}
```

---

## 4. 컨트롤러 및 글로벌 핸들러 사용 예시

### 4.1. Controller (성공 응답)
```java
@RestController
@RequestMapping("/api/v1/surveys")
@RequiredArgsConstructor
public class SurveyController {

    @PostMapping
    public ApiResponse<SurveyCreateResponse> createSurvey(@RequestBody @Valid SurveyCreateRequest request) {
        SurveyCreateResponse response = surveyService.createSurvey(request);
        return ApiResponse.created(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<SurveyDetailResponse> getSurvey(@PathVariable Long id) {
        SurveyDetailResponse response = surveyService.getSurvey(id);
        return ApiResponse.success(response);
    }
}
```

### 4.2. GlobalExceptionHandler (실패 응답)
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode, e.getMessage()));
    }
}
```
