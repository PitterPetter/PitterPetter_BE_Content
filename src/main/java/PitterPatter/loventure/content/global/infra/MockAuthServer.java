package PitterPatter.loventure.content.global.infra;

import PitterPatter.loventure.content.domain.diary.application.dto.response.UserProfileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

/**
 * 로컬 개발 환경에서 Auth 서비스 없이 테스트하기 위한 Mock REST API
 * 
 * /internal/user/{userId} 엔드포인트를 제공하여 실제 Auth 서비스처럼 동작합니다.
 * 
 * 내부 MSA 통신:
 * - Authorization 헤더 불필요 (이미 Content 서비스에서 JWT 인증 완료)
 * - userId만으로 사용자 정보 조회
 * 
 * 활성화 조건:
 * - application.yml에 mock.auth.enabled=true 설정 시 활성화 (기본값: true)
 * 
 * 비활성화:
 * - application.yml에서 mock.auth.enabled: false 설정
 */
@Slf4j
@RestController
@RequestMapping("/internal/user")  // 단수형으로 변경
@ConditionalOnProperty(name = "mock.auth.enabled", havingValue = "true", matchIfMissing = true)
public class MockAuthServer {

    @GetMapping("/{userId}")
    public UserProfileResponse getUserById(@PathVariable Long userId) {
        
        log.info("🧪 [Mock Auth Server] GET /internal/user/{} 호출됨 (토큰 불필요)", userId);
        
        // userId에 따라 다른 Mock 데이터 반환 (테스트용)
        String mockName = switch (userId.intValue() % 5) {
            case 0 -> "김철수";
            case 1 -> "이영희";
            case 2 -> "박민수";
            case 3 -> "정지은";
            default -> "홍길동";
        };
        
        log.info("🧪 [Mock Auth Server] 반환 데이터 - userId: {}, name: {}", userId, mockName);
        
        return new UserProfileResponse(userId.toString(), mockName);  // String으로 변환
    }
}

