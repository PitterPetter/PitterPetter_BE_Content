package PitterPatter.loventure.content.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"timestamp", "code", "message", "result"})
public class BaseResponse<T> {

    private final LocalDateTime timestamp = LocalDateTime.now();

    private final String code;

    // message도 추가하면 좋지 않을까?? 모든 예외를 코드로만 나오면 결국 무슨 에러인지 찾아봐야하니까
    //private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> BaseResponse<T> success(T result) {
        return new BaseResponse<>("COMMON200", result);
    }

    public static BaseResponse<Void> success() {
        return new BaseResponse<>("COMMON200", null);
    }

    public static <T> BaseResponse<T> failure(String code, T data) {
        return new BaseResponse<>(code, data);
    }
}
