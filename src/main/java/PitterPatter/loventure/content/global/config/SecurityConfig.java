package PitterPatter.loventure.content.global.config;

import PitterPatter.loventure.content.global.security.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    public SecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("=== SecurityConfig CORS 설정 초기화 ===");
        
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 Origin 설정 - 구체적인 도메인 지정
        configuration.addAllowedOriginPattern("https://loventure.us");
        configuration.addAllowedOriginPattern("https://*.loventure.us"); // 하위 도메인 포함
        configuration.addAllowedOriginPattern("http://localhost:*"); // 로컬 개발 환경
        configuration.addAllowedOriginPattern("http://127.0.0.1:*");
        
        log.info("허용된 Origin 패턴: {}", configuration.getAllowedOriginPatterns());
        
        // 허용할 HTTP 메서드
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("PATCH");
        
        log.info("허용된 메서드: {}", configuration.getAllowedMethods());
        
        // 허용할 헤더
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("X-User-Id");
        configuration.addExposedHeader("X-Couple-Id");
        
        log.info("허용된 헤더: {}", configuration.getAllowedHeaders());
        log.info("노출된 헤더: {}", configuration.getExposedHeaders());
        
        // 쿠키 및 인증 정보 허용
        configuration.setAllowCredentials(true);
        
        // preflight 요청 캐시 시간 (1시간)
        configuration.setMaxAge(3600L);
        
        log.info("Credentials 허용: {}", configuration.getAllowCredentials());
        log.info("Preflight 캐시 시간: {}초", configuration.getMaxAge());
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("=== SecurityConfig CORS 설정 완료 ===");
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI 및 API 문서 허용
                        .requestMatchers("/docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/diaries/**").permitAll()
                        .requestMatchers("/api/diaries/swagger-ui/**", "/api/diaries/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/diaries/swagger-ui.html", "/api/diaries/swagger-ui/**", "/api/diaries/v3/api-docs/**").permitAll() // 다이어리 API 문서 및 스웨거 리소스 허용
                        
                        // Actuator Health Check 허용
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // CORS preflight 요청 허용
                        .requestMatchers("OPTIONS", "/**").permitAll()
                        
                        // 배포 후 테스트를 위해 임시로 모든 API 허용 (추후 인증 활성화 필요)
                        // TODO: 테스트 이후 인증 활성화
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(tokenProvider.getJwtDecoder())));
        return http.build();
    }
}