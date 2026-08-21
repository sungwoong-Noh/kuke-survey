package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import exception.ErrorCode;
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


    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, data, null, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(int status, T data, String message) {
        return new ApiResponse<>(true, status, data, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, 201, data, null, null, LocalDateTime.now());
    }

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
