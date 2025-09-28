package PitterPatter.loventure.content.global.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 핸들러
 * 
 * 이 클래스는 @RestControllerAdvice 어노테이션을 사용하여
 * 모든 컨트롤러에서 발생하는 예외를 중앙에서 처리합니다.
 * 
 * 처리하는 예외 종류:
 * 1. MethodArgumentNotValidException: @Valid 검증 실패
 * 2. BindException: 데이터 바인딩 실패
 * 3. CustomException: 비즈니스 로직 예외
 * 4. Exception: 기타 모든 예외 (최종 fallback)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 어노테이션으로 인한 검증 실패 예외 처리
     * 주로 Request Body의 유효성 검증 실패 시 발생
     * 
     * @param ex 검증 실패 예외
     * @param request HTTP 요청 정보
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        // 첫 번째 검증 오류 메시지를 추출
        String message = ex.getBindingResult().getAllErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage())
            .orElse("Validation error");
        
        ErrorResponse body = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            message,
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 데이터 바인딩 실패 예외 처리
     * 주로 요청 파라미터를 객체로 변환할 때 실패하는 경우
     * 
     * @param ex 바인딩 실패 예외
     * @param request HTTP 요청 정보
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBind(BindException ex, HttpServletRequest request) {
        // 첫 번째 바인딩 오류 메시지를 추출
        String message = ex.getAllErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage())
            .orElse("Bind error");
        
        ErrorResponse body = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            message,
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 커스텀 비즈니스 로직 예외 처리
     * 
     * 이 메서드는 CustomException을 처리하여 ErrorCode에 정의된
     * HTTP 상태 코드와 메시지를 사용해 응답을 생성합니다.
     * 
     * @param ex 커스텀 예외
     * @param request HTTP 요청 정보
     * @return ErrorCode에 정의된 HTTP 상태 코드로 응답
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        
        // ErrorCode에 정의된 HTTP 상태 코드와 메시지를 사용
        ErrorResponse body = ErrorResponse.of(
            errorCode.getHttpStatus().value(),      // HTTP 상태 코드 (예: 401)
            errorCode.getHttpStatus().getReasonPhrase(), // HTTP 상태 메시지 (예: "Unauthorized")
            ex.getMessage(),                        // 실제 에러 메시지
            request.getRequestURI()                 // 요청 경로
        );
        
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    /**
     * 모든 예외의 최종 처리기 (Fallback)
     * 
     * 위에서 처리되지 않은 모든 예외를 여기서 처리합니다.
     * 주로 예상하지 못한 시스템 오류나 런타임 예외가 여기에 해당됩니다.
     * 
     * @param ex 발생한 예외
     * @param request HTTP 요청 정보
     * @return 500 Internal Server Error 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception ex, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}


