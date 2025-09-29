package PitterPatter.loventure.content.global.config;

import PitterPatter.loventure.content.global.security.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Spring MVC 설정 클래스
 * 
 * @CurrentUser 어노테이션을 사용하기 위해 CurrentUserArgumentResolver를 등록합니다.
 * 이 설정이 있어야 @CurrentUser 어노테이션이 동작합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    public WebConfig(CurrentUserArgumentResolver currentUserArgumentResolver) {
        this.currentUserArgumentResolver = currentUserArgumentResolver;
    }

    /**
     * 커스텀 Argument Resolver를 Spring MVC에 등록
     * 
     * 이 메서드가 호출되면 Spring이 @CurrentUser 어노테이션을 인식하고
     * CurrentUserArgumentResolver를 사용해서 자동으로 값을 주입합니다.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}
