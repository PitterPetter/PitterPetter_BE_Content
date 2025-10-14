package PitterPatter.loventure.content.global.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 모든 Long/long 타입을 JSON 직렬화 시 String으로 변환
 * Integer, Double 등 다른 숫자 타입은 영향 없음
 * 
 * 설정 전: {"diaryId": 1234567890123456789}
 * 설정 후: {"diaryId": "1234567890123456789"}
 */
@Configuration
public class JacksonConfig {

    /**
     * Long 타입을 String으로 직렬화하는 커스터마이저
     * 
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder
                // Long 객체 타입을 String으로 직렬화
                .serializerByType(Long.class, ToStringSerializer.instance)
                // long primitive 타입을 String으로 직렬화
                .serializerByType(Long.TYPE, ToStringSerializer.instance);
    }
}

