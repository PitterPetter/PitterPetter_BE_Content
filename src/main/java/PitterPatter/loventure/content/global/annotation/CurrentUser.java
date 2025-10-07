package PitterPatter.loventure.content.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 로그인한 사용자의 정보를 자동으로 주입받기 위한 어노테이션
 */
@Target(ElementType.PARAMETER)  // 메서드 파라미터에만 사용 가능
@Retention(RetentionPolicy.RUNTIME)  // 런타임에 어노테이션 정보 유지
public @interface CurrentUser {
}
