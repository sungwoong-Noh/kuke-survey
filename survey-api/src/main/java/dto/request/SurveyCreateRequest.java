package dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "설문조사 생성 요청 DTO")
public record SurveyCreateRequest(
        @Schema(description = "설문조사 제목", example = "2026 서비스 이용 만족도 조사", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "설문 제목은 필수입니다.")
        @Size(max = 100, message = "설문 제목ㅇ느 최대 100자까지 가능합니다.")
        String title,

        @Schema(description = "설문조사 설명", example = "서비스 개선을 위한 고객 의견 수렴 설문입니다.")
        @Size(max = 100, message = "설문 설명은 최대 1,000자까지 가능합니다.")
        String description
) {

}
