package PitterPatter.loventure.content.global.error;

import lombok.Getter;

/**
 * 애플리케이션에서 발생하는 비즈니스 로직 예외를 위한 커스텀 예외 클래스
 * 
 * 이 클래스는 ErrorCode를 포함하여 일관된 에러 처리를 가능하게 합니다.
 * GlobalExceptionHandler에서 이 예외를 잡아서 적절한 HTTP 응답으로 변환합니다.
 * 
 * 사용 예시:
 * - throw new CustomException(ErrorCode.DIARY401);
 * - throw new CustomException(ErrorCode.DIARY401, "커스텀 메시지");
 */
@Getter
public class CustomException extends RuntimeException {
    
    /** 발생한 에러의 코드 정보 */
    private final ErrorCode errorCode;
    
    /**
     * 기본 생성자 - ErrorCode의 기본 메시지를 사용
     * 
     * @param errorCode 에러 코드
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    /**
     * 커스텀 메시지를 사용하는 생성자
     * 
     * @param errorCode 에러 코드
     * @param message 커스텀 에러 메시지
     */
    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 원인 예외와 함께 기본 메시지를 사용하는 생성자
     * 
     * @param errorCode 에러 코드
     * @param cause 원인이 되는 예외
     */
    public CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 원인 예외와 커스텀 메시지를 함께 사용하는 생성자
     * 
     * @param errorCode 에러 코드
     * @param message 커스텀 에러 메시지
     * @param cause 원인이 되는 예외
     */
    public CustomException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
