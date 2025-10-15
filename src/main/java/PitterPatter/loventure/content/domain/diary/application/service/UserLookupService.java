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

    /**
     * Auth 서비스에서 사용자 이름 조회
     *
     * @param userId 조회할 사용자 ID (이미 JWT 인증된 상태이므로 신뢰 가능)
     * @return 사용자 이름
     */
    public String getUserName(String userId) {
        try {
            // 내부 MSA 통신: userId만으로 사용자 정보 조회 (토큰 불필요)
            UserProfileResponse profile = authClient.getUserById(userId);

            // null 체크
            if (profile == null || profile.userName() == null) {
                log.error("Auth 서비스에서 null 응답을 받았습니다. userId: {}", userId);
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }

            return profile.userName();

        } catch (FeignException.NotFound e) {
            log.error("사용자를 찾을 수 없습니다. userId: {}", userId, e);
            throw new CustomException(ErrorCode.USER_NOT_FOUND);

        } catch (FeignException e) {
            log.error("Auth 서비스 호출 중 오류가 발생했습니다. userId: {}, status: {}",
                     userId, e.status(), e);

            // Feign 예외를 그대로 던져서 GlobalExceptionHandler가 처리하도록 함
            throw e;

        } catch (Exception e) {
            log.error("예상치 못한 오류가 발생했습니다. userId: {}", userId, e);
            throw new CustomException(ErrorCode.AUTH_SERVICE_UNAVAILABLE);
        }
    }
}
