package exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class BusinessExceptionTest {

    @Test
    @DisplayName("ErrorCode로 BusinessException을 생성하면 에러코드와 메시지가 정상 매핑된다.")
    void createBusinessException_WithErrorCode() {
        //given
        ErrorCode errorCode = CommonErrorCode.INVALID_INPUT_VALUE;

        //when
        BusinessException businessException = new BusinessException(errorCode);

        //then
        assertThat(errorCode.getCode()).isEqualTo(businessException.getErrorCode().getCode());
    }

    @Test
    @DisplayName("커스텀 메시지로 BusinessException을 생성하면 커스텀 메시지가 우선 적용된다")
    void createBusinessException_WithCustomMessage() {
        //given
        CommonErrorCode errorCode = CommonErrorCode.INVALID_INPUT_VALUE;
        String customMessage = "커스텀 메시지 테스트";

        //when
        BusinessException businessException = new BusinessException(errorCode, customMessage);

        //then
        assertThat(businessException.getErrorCode().getCode()).isEqualTo(errorCode.getCode());
        assertThat(businessException.getMessage()).isEqualTo(customMessage);

    }
}