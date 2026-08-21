package exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    // 400 BAD_REQUEST (입력값 검증 및 요청 형식 오류)
    INVALID_INPUT_VALUE(400, "COMMON_400_001", "유효하지 않은 입력값입니다."),
    INVALID_TYPE_VALUE(400, "COMMON_400_002", "요청 데이터의 타입이 올바르지 않습니다."),
    MISSING_REQUIRED_PARAMETER(400, "COMMON_400_003", "필수 파라미터가 누락되었습니다."),
    HTTP_MESSAGE_NOT_READABLE(400, "COMMON_400_004", "요청 본문(JSON)을 파싱할 수 없습니다."),


    // 404 NOT_FOUND (엔드포인트 또는 공통 리소스 미존재)
    RESOURCE_NOT_FOUND(404, "COMMON_404_001", "요청한 리소스를 찾을 수 없습니다."),

    //405 METHOD_NOT_ALLOW (지원하지 않는 HTTP 메서드)
    METHOD_NOT_ALLOWED(405, "COMMON_405_001", "지원하지 않는 HTTP 요청 방식입니다."),

    // 500 INTERNAL_SERVER_ERROR (서버 내부 에러)
    INTERNAL_SERVER_ERROR(500, "COMMON_500_001", "서버 내부 오류가 발생했습니다.")
    ;




    private final int status;
    private final String code;
    private final String message;
}
