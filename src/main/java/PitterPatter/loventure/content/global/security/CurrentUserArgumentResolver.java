package PitterPatter.loventure.content.global.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @CurrentUser 어노테이션이 붙은 파라미터를 자동으로 처리하는 리졸버
 * 
 * 동작 과정:
 * 1. 컨트롤러 메서드에서 @CurrentUser 어노테이션 감지
 * 2. Authorization 헤더에서 JWT 토큰 추출
 * 3. JWT 토큰을 파싱하여 userId 또는 다른 값 추출
 * 4. 컨트롤러 파라미터에 자동 주입
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenProvider tokenProvider;

    public CurrentUserArgumentResolver(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * 이 리졸버가 처리할 수 있는 파라미터인지 확인
     * @param parameter 메서드 파라미터 정보
     * @return @CurrentUser 또는 @CurrentCouple 어노테이션이 있으면 true
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) || 
               parameter.hasParameterAnnotation(CurrentCouple.class);
    }

    /**
     * 실제로 파라미터 값을 추출하고 주입하는 메서드
     * 
     * @param parameter 메서드 파라미터 정보
     * @param mavContainer ModelAndView 컨테이너
     * @param webRequest HTTP 요청 정보
     * @param binderFactory 데이터 바인더 팩토리
     * @return 추출된 값 (userId, 토큰 등)
     * @throws Exception JWT 파싱 오류 등
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        
        // 1. HTTP 요청에서 Authorization 헤더 추출
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authHeader = request.getHeader("Authorization");
        
        // 2. Authorization 헤더 유효성 검사
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }
        
        // 3. 어노테이션과 파라미터 타입에 따라 다른 값 반환
        Class<?> parameterType = parameter.getParameterType();
        
        if (parameterType == Long.class) {
            if (parameter.hasParameterAnnotation(CurrentUser.class)) {
                // @CurrentUser Long userId -> JWT에서 userId 값 추출
                String token = authHeader.substring(7); // "Bearer " 제거
                return tokenProvider.getUserIdFromToken(token);
            } else if (parameter.hasParameterAnnotation(CurrentCouple.class)) {
                // @CurrentCouple Long coupleId -> JWT에서 coupleId 값 추출
                return tokenProvider.extractCoupleId(authHeader);
            }
        } else if (parameterType == String.class) {
            // String 타입이면 토큰 자체 반환 (Bearer 제거)
            // 예: @CurrentUser String token -> "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            return authHeader.substring(7); // "Bearer " 제거
        }
        
        // 지원하지 않는 타입이면 예외 발생
        throw new IllegalArgumentException("Unsupported parameter type: " + parameterType);
    }
}
