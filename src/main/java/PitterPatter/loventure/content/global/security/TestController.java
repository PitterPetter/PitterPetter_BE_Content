package PitterPatter.loventure.content.global.security;

import PitterPatter.loventure.content.global.annotation.CurrentCouple;
import PitterPatter.loventure.content.global.annotation.CurrentUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * JWT 토큰 테스트를 위한 컨트롤러
 * 개발/테스트 환경에서만 사용하세요!
 */
@RestController
@RequestMapping("/test")
@Tag(name = "JWT 토큰 테스트", description = "JWT 토큰 생성 및 테스트 API")
@Profile({"dev", "test"}) // dev, test 프로필에서만 활성화
@ConditionalOnProperty(name = "app.test-endpoints.enabled", havingValue = "true", matchIfMissing = false)
public class TestController {

    private final JwtTokenGenerator jwtTokenGenerator;

    public TestController(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    /**
     * JWT 토큰 생성 엔드포인트
     * 이 토큰을 Swagger의 Authorization에 입력하여 API를 테스트하세요.
     * 
     * @param userId 사용자 ID (기본값: 123)
     * @param coupleId 커플 ID (기본값: 456)
     * @return 생성된 JWT 토큰
     */
    @GetMapping("/token")
    @Operation(summary = "JWT 토큰 생성", description = "테스트용 JWT 토큰을 생성합니다. 생성된 토큰을 Swagger Authorization에 입력하세요.")
    public String generateToken(
            @Parameter(description = "사용자 ID", example = "123") 
            @RequestParam(defaultValue = "123") Long userId,
            @Parameter(description = "커플 ID", example = "456") 
            @RequestParam(defaultValue = "456") Long coupleId
    ) {
        String token = jwtTokenGenerator.generateToken(userId, coupleId);
        return String.format("""
                JWT 토큰이 생성되었습니다!
                
                토큰: %s
                
                사용 방법:
                1. 위 토큰을 복사하세요
                2. Swagger UI에서 "Authorize" 버튼을 클릭하세요
                3. "Bearer " + 토큰을 입력하세요 (예: Bearer %s)
                4. 이제 인증이 필요한 API를 테스트할 수 있습니다.
                """, token, token);
    }

    /**
     * @CurrentUser 어노테이션 테스트 엔드포인트
     * JWT 토큰에서 사용자 ID를 추출하는 테스트
     * 
     * @param userId JWT에서 추출된 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/user")
    @Operation(summary = "현재 사용자 정보 조회", description = "JWT 토큰에서 추출된 사용자 ID를 반환합니다.")
    public String testCurrentUser(@CurrentUser Long userId) {
        return "현재 사용자 ID: " + userId;
    }

    /**
     * @CurrentCouple 어노테이션 테스트 엔드포인트
     * JWT 토큰에서 커플 ID를 추출하는 테스트
     * 
     * @param coupleId JWT에서 추출된 커플 ID
     * @return 커플 정보
     */
    @GetMapping("/couple")
    @Operation(summary = "현재 커플 정보 조회", description = "JWT 토큰에서 추출된 커플 ID를 반환합니다.")
    public String testCurrentCouple(@CurrentCouple Long coupleId) {
        return "현재 커플 ID: " + coupleId;
    }

    /**
     * @CurrentUser와 @CurrentCouple 어노테이션 동시 테스트 엔드포인트
     * JWT 토큰에서 사용자 ID와 커플 ID를 동시에 추출하는 테스트
     * 
     * @param userId JWT에서 추출된 사용자 ID
     * @param coupleId JWT에서 추출된 커플 ID
     * @return 사용자와 커플 정보
     */
    @GetMapping("/user-couple")
    @Operation(summary = "현재 사용자와 커플 정보 조회", description = "JWT 토큰에서 추출된 사용자 ID와 커플 ID를 반환합니다.")
    public String testCurrentUserAndCouple(@CurrentUser Long userId, @CurrentCouple Long coupleId) {
        return "사용자 ID: " + userId + ", 커플 ID: " + coupleId;
    }
}
