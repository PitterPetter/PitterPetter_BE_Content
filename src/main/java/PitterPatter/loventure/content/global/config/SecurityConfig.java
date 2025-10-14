package PitterPatter.loventure.content.global.config;

import PitterPatter.loventure.content.global.security.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    public SecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI 및 API 문서 허용
                        .requestMatchers("/docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/diaries/**").permitAll()
                        .requestMatchers("/api/diaries/swagger-ui/**", "/api/diaries/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/diaries/swagger-ui.html", "/api/diaries/swagger-ui/**", "/api/diaries/v3/api-docs/**").permitAll() // 다이어리 API 문서 및 스웨거 리소스 허용
                        
                        // Actuator Health Check 허용
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // 배포 후 테스트를 위해 임시로 모든 API 허용 (추후 인증 활성화 필요)
                        // TODO: 테스트 이후 인증 활성화
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(tokenProvider.getJwtDecoder())));
        return http.build();
    }
}