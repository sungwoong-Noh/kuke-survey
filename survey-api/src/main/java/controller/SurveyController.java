package controller;

import dto.request.SurveyCreateRequest;
import dto.response.SurveyCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

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

        return ApiResponse.success(new SurveyCreateResponse());
    }

}
