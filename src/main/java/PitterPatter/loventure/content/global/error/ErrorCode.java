package PitterPatter.loventure.content.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전체에서 사용되는 에러 코드를 정의하는 Enum
 * 
 * 각 에러 코드는 다음 정보를 포함합니다:
 * - code: 에러 코드 (예: "DIARY401")
 * - message: 기본 에러 메시지 (예: "다이어리 수정 권한이 없습니다")
 * - httpStatus: HTTP 응답 상태 코드 (예: HttpStatus.UNAUTHORIZED)
 * 
 * 새로운 에러 코드를 추가할 때는 이 Enum에만 추가하면 됩니다.
 * GlobalExceptionHandler가 자동으로 처리합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // ===== 공통 에러 코드 =====
    COMMON200("COMMON200", "성공", HttpStatus.OK),
    COMMON400("COMMON400", "잘못된 요청입니다", HttpStatus.BAD_REQUEST),
    COMMON401("COMMON401", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    COMMON403("COMMON403", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    COMMON404("COMMON404", "리소스를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    COMMON500("COMMON500", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // ===== 다이어리 관련 에러 코드 =====
    DIARY401("DIARY401", "다이어리 접근 권한이 없습니다", HttpStatus.UNAUTHORIZED),
    DIARY402("DIARY402", "커플 정보가 일치하지 않습니다", HttpStatus.FORBIDDEN),
    DIARY404("DIARY404", "다이어리를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    DIARY409("DIARY409", "다이어리 수정에 실패했습니다", HttpStatus.CONFLICT),
    
    // ===== 댓글 관련 에러 코드 =====
    COMMENT401("COMMENT401", "댓글 수정/삭제 권한이 없습니다", HttpStatus.UNAUTHORIZED),
    COMMENT404("COMMENT404", "댓글을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    
    // ===== Auth 서비스 통신 관련 에러 코드 =====
    AUTH_SERVICE_UNAVAILABLE("AUTH500", "Auth 서비스와 통신할 수 없습니다", HttpStatus.SERVICE_UNAVAILABLE),
    USER_NOT_FOUND("AUTH404", "해당 사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    AUTH_SERVICE_ERROR("AUTH502", "Auth 서비스에서 오류가 발생했습니다", HttpStatus.BAD_GATEWAY);
    
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
    
    /**
     * 에러 코드 문자열로부터 ErrorCode Enum을 찾는 메서드
     * 
     * @param code 찾고자 하는 에러 코드 문자열
     * @return 해당하는 ErrorCode Enum
     * @throws IllegalArgumentException 해당하는 에러 코드가 없을 경우
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("Unknown error code: " + code);
    }
}
