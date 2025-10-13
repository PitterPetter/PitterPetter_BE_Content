package PitterPatter.loventure.content.domain.diary.application.dto.response;

/**
 * Auth 서비스로부터 받는 사용자 프로필 응답
 * 
 * Auth 서비스와 약속된 형식:
 * - userId: String 타입 (Auth 서비스의 userId 형식)
 * - name: 사용자 이름
 */
public record UserProfileResponse(
        String userId,
        String name
) {
}
