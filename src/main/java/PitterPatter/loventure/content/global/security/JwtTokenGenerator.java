package PitterPatter.loventure.content.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 토큰 생성을 위한 간단한 유틸리티 클래스
 * 테스트용으로만 사용하세요!
 */
@Component
public class JwtTokenGenerator {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * 테스트용 JWT 토큰 생성
     * @param userId 사용자 ID
     * @param coupleId 커플 ID
     * @return JWT 토큰 문자열
     */
    public String generateToken(Long userId, Long coupleId) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("coupleId", coupleId);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1시간
                .signWith(key, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }
}
