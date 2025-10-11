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
                        .requestMatchers("/test/token").permitAll() // JWT 토큰 생성 엔드포인트는 인증 불필요
                        .requestMatchers("/test/user", "/test/couple", "/test/user-couple").authenticated() // 테스트 엔드포인트는 JWT 인증 필요
                        .requestMatchers("/docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI 완전 허용
                        .requestMatchers("/api/diaries/swagger-ui.html", "/api/diaries/swagger-ui/**", "/api/diaries/v3/api-docs/**").permitAll() // 다이어리 API 문서 및 스웨거 리소스 허용
                        .requestMatchers("/api/diaries/**").authenticated() // 다이어리 API는 JWT 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(tokenProvider.getJwtDecoder())));
        return http.build();
    }
}