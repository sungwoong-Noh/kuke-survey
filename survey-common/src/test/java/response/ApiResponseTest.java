package response;


import exception.CommonErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    @DisplayName("데이터를 담아 성공 응답을 생성하면 200 상태코드와 success=true가 설정된다.")
    void createSuccessResponse() {
        //given
        String testData = "테스트 데이터";

        //when
        ApiResponse<String> response = ApiResponse.created(testData);

        //then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(testData);
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getTimestamp()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("ErrorCode로 에러 응답을 생성하면 success=falsed와 에러 상세 정보가 설정된다")
    void createErrorResponse_WithErrorCode() {
        //given
        CommonErrorCode errorCode = CommonErrorCode.RESOURCE_NOT_FOUND;

        //when
        ApiResponse<Object> error = ApiResponse.error(errorCode);

        //then
        assertThat(error.getError().code()).isEqualTo("COMMON_404_001");
        assertThat(error.getError().message()).isEqualTo("요청한 리소스를 찾을 수 없습니다.");
        assertThat(error.getStatus()).isEqualTo(404);
        assertThat(error.getTimestamp()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("필드 에러 목록을 포함하여 에러 응답을 생성할 수 있다")
    void createErrorResponse_WithFiledErrors() {
        //given
        CommonErrorCode errorCode = CommonErrorCode.INVALID_INPUT_VALUE;
        List<FieldErrorDetail> fieldErrors = List.of(
                new FieldErrorDetail("title", "", "제목은 필수입니다."),
                new FieldErrorDetail("status", null, "상태값은 필수입니다.")
        );

        //when
        ApiResponse<Object> error = ApiResponse.error(errorCode, fieldErrors);

        //when
        assertThat(error.getError().fieldErrors().size()).isEqualTo(2);
        assertThat(error.getError().fieldErrors().get(0).field()).isEqualTo("title");
        assertThat(error.getError().fieldErrors().get(0).reason()).isEqualTo("제목은 필수입니다.");
        assertThat(error.getError().fieldErrors().get(1).field()).isEqualTo("status");
        assertThat(error.getError().fieldErrors().get(1).reason()).isEqualTo("상태값은 필수입니다.");
    }
}