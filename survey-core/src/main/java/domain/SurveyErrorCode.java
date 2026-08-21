package domain;

import exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SurveyErrorCode implements ErrorCode {


    // 400 BAD_REQUEST
    INVALID_SURVEY_TITLE(400, "SURVEY_400_001", "설문 제목은 필수이며 100자 이하이어야 합니다."),
    QUESTION_COUNT_LIMIT(400, "SURVEY_400_002", "질문은 최대 10개까지만 등록할 수 있습니다."),
    INVALID_QUESTION_OPTION(400, "SURVEY_400_003", "선택형 질문은 최소 1개 이상의 보기가 필요합니다."),
    OPTION_NOT_ALLOWED(400, "SURVEY_400_004", "주관식 문항에는 보기를 등록할 수 없습니다."),

    // 404 NOT_FOUND
    SURVEY_NOT_FOUND(404, "SURVEY_404_001", "해당 설문조사를 찾을 수 없습니다."),

    // 409 CONFLICT (상태 충돌)
    SURVEY_ALREADY_PUBLISHED(409, "SURVEY_409_001", "이미 배포된 설문조사는 수정할 수 없습니다."),
    CANNOT_PUBLISHED_EMPTY_SURVEY(409, "SURVEY_409_002", "질문이 최소 1개 이상 등록되어야 배포할 수 있습니다.")

    ;



    private final int status;
    private final String code;
    private final String message;

    }
