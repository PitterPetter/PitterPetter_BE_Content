package PitterPatter.loventure.content.global.infra;

import PitterPatter.loventure.content.domain.diary.application.dto.response.UserProfileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

/**
 * 로컬 개발 환경에서 Auth 서비스 없이 테스트하기 위한 Mock REST API
 * 
 * @Profile("local") 어노테이션으로 로컬 환경에서만 활성화됩니다.
 * 
 * 활성화 조건:
 * - spring.profiles.active=local 설정 시 활성화
 * 
 * 배포 환경 (prod):
 * - 자동으로 비활성화되어 실제 Auth 서비스 사용
 */
@Slf4j
@Profile("local")  // 로컬 환경에서만 활성화!
@RestController
@RequestMapping("/internal/user")
public class MockAuthServer {

    @GetMapping("/{userId}")
    public UserProfileResponse getUserById(@PathVariable Long userId) {
        
        log.info("🧪 [Mock Auth Server] GET /internal/user/{} 호출됨", userId);
        
        // userId에 따라 다른 Mock 데이터 반환 (테스트용)
        String mockName = switch (userId.intValue() % 5) {
            case 0 -> "김철수";
            case 1 -> "이영희";
            case 2 -> "박민수";
            case 3 -> "정지은";
            default -> "홍길동";
        };
        
        log.info("🧪 [Mock Auth Server] 반환 데이터 - userId: {}, name: {}", userId, mockName);
        
        return new UserProfileResponse(userId.toString(), mockName);
    }
}

