# API 문서화 자동화 규격 (API Documentation Specification)

## 1. 설계 원칙 및 목표
- **OpenAPI 3.0 / Swagger UI 표준 준수**: 프론트엔드 및 외부 연동 시 실시간으로 테스트 가능한 대화형 API 명세서를 제공한다.
- **코드 기반 자동 동기화**: 컨트롤러 및 DTO 코드 변경 시 API 문서가 자동으로 동기화되도록 `springdoc-openapi` 라이브러리를 활용한다.
- **표준 응답(`ApiResponse<T>`) 연동**: 공통 응답 포맷과 예외 에러 코드가 Swagger UI에 명확하게 표현되도록 설정한다.
- **배치 모듈**: `survey-api` 모듈에 설정 및 의존성을 구성한다.

---

## 2. 기술 스택 및 의존성

* **라이브러리**: `springdoc-openapi-starter-webmvc-ui` (Spring Boot 3.x 지원)
* **설정 위치**: `survey-api/build.gradle.kts`

```kotlin
// survey-api/build.gradle.kts
dependencies {
    // OpenAPI 3.0 / Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
}
```

---

## 3. Swagger 접속 경로 및 설정 (`survey-api/src/main/resources/application.yml`)

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html        # 접속 URL: http://localhost:8080/swagger-ui.html
    groups-order: DESC
    operations-sorter: method     # API 메서드 정렬 (GET, POST, PUT, DELETE 등)
    tags-sorter: alpha            # 태그 알파벳순 정렬
    display-request-duration: true # API 응답 시간 표시
  api-docs:
    path: /v3/api-docs            # OpenAPI 스펙 JSON 경로
```

---

## 4. OpenAPI 전역 설정 클래스 (`survey-api`)

```java
package com.kuke.survey.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kuke Survey Service API")
                        .description("설문조사 생성, 배포, 응답 수집을 위한 백엔드 REST API 명세서입니다.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Kuke Survey Team")));
    }
}
```

---

## 5. Swagger 어노테이션 작성 컨벤션

### 5.1. Controller 레벨 (`@Tag`, `@Operation`)
* `@Tag`: 컨트롤러 단위 그룹화 (이름 및 설명 필수)
* `@Operation`: 엔드포인트별 요약(`summary`)과 상세 설명(`description`) 작성

```java
package com.kuke.survey.api.controller;

import com.kuke.survey.api.dto.request.SurveyCreateRequest;
import com.kuke.survey.api.dto.response.SurveyCreateResponse;
import com.kuke.survey.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. 설문 양식 관리", description = "설문조사 생성, 수정, 질문 추가, 배포 관련 API")
@RestController
@RequestMapping("/api/v1/surveys")
public class SurveyController {

    @Operation(
        summary = "설문조사 신규 생성",
        description = "제목과 설명을 받아 새로운 설문조사 양식을 DRAFT 상태로 생성합니다."
    )
    @PostMapping
    public ApiResponse<SurveyCreateResponse> createSurvey(
            @RequestBody @Valid SurveyCreateRequest request
    ) {
        // ...
        return ApiResponse.created(response);
    }
}
```

### 5.2. Request / Response DTO 레벨 (`@Schema`)
* `@Schema`: 필드별 설명(`description`), 필수 여부(`requiredMode`), 예시값(`example`) 작성

```java
package com.kuke.survey.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "설문조사 생성 요청 DTO")
public record SurveyCreateRequest(

    @Schema(description = "설문조사 제목", example = "2026 서비스 이용 만족도 조사", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "설문 제목은 필수입니다.")
    @Size(max = 100, message = "설문 제목은 최대 100자까지 가능합니다.")
    String title,

    @Schema(description = "설문조사 설명", example = "서비스 개선을 위한 고객 의견 수렴 설문입니다.")
    @Size(max = 1000, message = "설문 설명은 최대 1000자까지 가능합니다.")
    String description
) {
}
```

---

## 6. 테스트 및 검증 방법
1. `./gradlew :survey-api:bootRun` 실행
2. 브라우저에서 `http://localhost:8080/swagger-ui.html` 접속
3. 등록된 API 목록, 파라미터 스키마, `Try it out`을 통한 실제 요청/응답 동작 확인
