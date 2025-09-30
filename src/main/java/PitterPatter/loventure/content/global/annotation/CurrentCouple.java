package PitterPatter.loventure.content.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 로그인한 사용자의 커플 ID를 자동으로 주입받기 위한 어노테이션
 * 
 * 사용법:
 * @GetMapping("/api/diaries")
 * public BaseResponse<List<DiaryResponse>> getDiaries(
 *     @CurrentUser Long userId,
 *     @CurrentCouple Long coupleId
 * ) {
 *     // userId와 coupleId가 JWT 토큰에서 자동으로 추출되어 주입됩니다
 * }
 */
@Target(ElementType.PARAMETER)  // 메서드 파라미터에만 사용 가능
@Retention(RetentionPolicy.RUNTIME)  // 런타임에 어노테이션 정보 유지
public @interface CurrentCouple {
}
