package PitterPatter.loventure.content.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 테스트용 JWT 토큰 생성기
 * 
 * ⚠️ local 환경에서만 활성화됩니다.
 * 프로덕션 환경에서는 사용할 수 없습니다.
 */
@Slf4j
@Component
@Profile("local")  // local 환경에서만 활성화
public class TestTokenGenerator {
    
    private final SecretKey key;
    private final long expiration;
    
    public TestTokenGenerator(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
        log.warn("⚠️ TestTokenGenerator is activated! This should only be used in LOCAL environment.");
    }
    
    /**
     * 테스트용 JWT 토큰 생성
     * 
     * @param userId 사용자 ID
     * @param coupleId 커플 ID
     * @return JWT 토큰 문자열
     */
    public String generateTestToken(Long userId, Long coupleId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))  // subject에 userId
                .claim("userId", userId)
                .claim("coupleId", coupleId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        
        log.info("Test token generated: userId={}, coupleId={}, expiresAt={}", userId, coupleId, expiryDate);
        
        return token;
    }
}

