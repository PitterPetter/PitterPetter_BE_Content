package PitterPatter.loventure.content.domain.diary.application.service;

import PitterPatter.loventure.content.domain.diary.application.dto.response.UserProfileResponse;
import PitterPatter.loventure.content.global.infra.AuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLookupService {

    private final AuthClient authClient;

    public String getUserName(Long targetUserId, String rawToken) {
        // 전송(→): 이 줄 호출 시 Feign이 GET /internal/users/{id} 요청을 Authorization 헤더와 함께 전송
        UserProfileResponse profile = authClient.getUserById(targetUserId, "Bearer " + rawToken);

        // 수신(←): 응답 JSON이 UserProfileResponse DTO로 역직렬화되어 반환됨
        return profile.name();
    }
}
