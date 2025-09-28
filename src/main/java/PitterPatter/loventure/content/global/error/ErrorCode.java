package PitterPatter.loventure.content.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // Common Error Codes
    COMMON200("COMMON200", "성공", HttpStatus.OK),
    COMMON400("COMMON400", "잘못된 요청입니다", HttpStatus.BAD_REQUEST),
    COMMON401("COMMON401", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    COMMON403("COMMON403", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    COMMON404("COMMON404", "리소스를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    COMMON500("COMMON500", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // Diary Error Codes
    DIARY401("DIARY401", "다이어리 수정 권한이 없습니다", HttpStatus.UNAUTHORIZED),
    DIARY402("DIARY402", "커플 정보가 일치하지 않습니다", HttpStatus.FORBIDDEN),
    DIARY404("DIARY404", "다이어리를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    DIARY409("DIARY409", "다이어리 수정에 실패했습니다", HttpStatus.CONFLICT);
    
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
    
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("Unknown error code: " + code);
    }
}
