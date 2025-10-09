package PitterPatter.loventure.content.domain.diary.application.service;

import PitterPatter.loventure.content.domain.diary.application.dto.response.UserProfileResponse;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import PitterPatter.loventure.content.global.infra.AuthClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLookupService {

    private final AuthClient authClient;

    public String getUserName(Long targetUserId, String rawToken) {
        try {
            // 전송(→): 이 줄 호출 시 Feign이 GET /internal/users/{id} 요청을 Authorization 헤더와 함께 전송
            UserProfileResponse profile = authClient.getUserById(targetUserId, "Bearer " + rawToken);

            // null 체크
            if (profile == null || profile.name() == null) {
                log.error("Auth 서비스에서 null 응답을 받았습니다. userId: {}", targetUserId);
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }

            // 수신(←): 응답 JSON이 UserProfileResponse DTO로 역직렬화되어 반환됨
            return profile.name();
            
        } catch (FeignException.NotFound e) {
            log.error("사용자를 찾을 수 없습니다. userId: {}", targetUserId, e);
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
            
        } catch (FeignException e) {
            log.error("Auth 서비스 호출 중 오류가 발생했습니다. userId: {}, status: {}", 
                     targetUserId, e.status(), e);
            
            // Feign 예외를 그대로 던져서 GlobalExceptionHandler가 처리하도록 함
            throw e;
            
        } catch (Exception e) {
            log.error("예상치 못한 오류가 발생했습니다. userId: {}", targetUserId, e);
            throw new CustomException(ErrorCode.AUTH_SERVICE_UNAVAILABLE);
        }
    }
}
