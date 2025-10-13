package PitterPatter.loventure.content.global.config;

import PitterPatter.loventure.content.global.common.BaseResponse;
import PitterPatter.loventure.content.global.security.TestTokenGenerator;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트용 API
 * 
 * ⚠️ local 환경에서만 활성화됩니다.
 * 프로덕션 환경에서는 이 컨트롤러가 로드되지 않습니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@Profile("local")  // local 환경에서만 활성화
@Tag(name = "Test", description = "⚠️ 테스트용 API (로컬 환경 전용)")
public class TestController {
    
    private final TestTokenGenerator tokenGenerator;
    
    /**
     * 테스트용 JWT 토큰 발급
     * 
     * 로컬 환경에서 API 테스트 시 사용할 JWT 토큰을 발급합니다.
     * 
     * @param userId 사용자 ID (기본값: 1)
     * @param coupleId 커플 ID (기본값: 1)
     * @return JWT 토큰
     */
    @GetMapping("/token")
    @Operation(
        summary = "테스트용 JWT 토큰 발급 (로컬 전용)",
        description = "⚠️ 로컬 환경에서만 사용 가능합니다. userId와 coupleId를 포함한 JWT 토큰을 발급합니다."
    )
    public BaseResponse<TestTokenResponse> generateToken(
            @Parameter(description = "사용자 ID", example = "1")
            @RequestParam(defaultValue = "1") Long userId,
            
            @Parameter(description = "커플 ID", example = "1")
            @RequestParam(defaultValue = "1") Long coupleId
    ) {
        log.info("🧪 Test token generation requested: userId={}, coupleId={}", userId, coupleId);
        
        String token = tokenGenerator.generateTestToken(userId, coupleId);
        
        return BaseResponse.success(new TestTokenResponse(
            token,
            "Bearer " + token,
            userId,
            coupleId,
            "⚠️ 이 토큰은 테스트용입니다. 프로덕션에서 사용하지 마세요."
        ));
    }
    
    /**
     * 테스트용 토큰 응답 DTO
     */
    public record TestTokenResponse(
            String token,
            String authorizationHeader,
            Long userId,
            Long coupleId,
            String warning
    ) {
    }
}

