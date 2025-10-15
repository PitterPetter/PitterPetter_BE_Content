package PitterPatter.loventure.content.global.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class TokenProvider {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder jwtDecoder;

    // Auth Service와 동일한 방식으로 JWT Secret 처리
    public TokenProvider(@Value("${jwt.secret}") String secret) {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalArgumentException("JWT secret must not be blank");
        }
        
        // Base64 디코딩 로직 (Auth Service JWTUtil과 동일)
        SecretKey key;
        try {
            // Base64 인코딩된 값 -> 바이트 배열로 디코딩 시도
            byte[] decodedKey = Base64.getDecoder().decode(secret);
            key = new SecretKeySpec(decodedKey, "HmacSHA256");
            log.info("JWT 시크릿 키를 Base64 디코딩하여 설정했습니다. 길이: {}", decodedKey.length);
        } catch (IllegalArgumentException e) {
            // Base64 디코딩 실패 시 원본 문자열을 UTF-8 바이트로 변환
            key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            log.info("JWT 시크릿 키를 UTF-8 바이트로 변환하여 설정했습니다. 길이: {}", secret.getBytes(StandardCharsets.UTF_8).length);
        }
        
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    public String extractCoupleId(Jwt jwt) {
        return getClaimAsString(jwt, "coupleId");
    }

    public String extractCoupleId(String authorizationHeader) {
        String token = resolveToken(authorizationHeader);
        return getCoupleIdFromToken(token);
    }

    public JwtDecoder getJwtDecoder() {
        return jwtDecoder;
    }

    public String getCoupleIdFromToken(String token) {
        return getClaimAsString(token, "coupleId");
    }

    /**
     * JWT 토큰 문자열에서 userId 추출
     * @param token JWT 토큰 문자열 (Bearer 제거된 순수 토큰)
     * @return 사용자 ID (TSID String)
     */
    public String getUserIdFromToken(String token) {
        return getClaimAsString(token, "userId");
    }

    /**
     * JWT 객체에서 userId 추출
     * @param jwt 파싱된 JWT 객체
     * @return 사용자 ID (TSID String)
     */
    public String getUserIdFromJwt(Jwt jwt) {
        return getClaimAsString(jwt, "userId");
    }

    private String getClaimAsString(String token, String claimName) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return getClaimAsString(jwt, claimName);
        } catch (JwtException ex) {
            throw new IllegalArgumentException("Invalid JWT token", ex);
        }
    }

    private String getClaimAsString(Jwt jwt, String claimName) {
        Object claimValue = jwt.getClaims().get(claimName);
        if (claimValue instanceof String stringValue) {
            return stringValue;
        }
        if (claimValue instanceof Number number) {
            return String.valueOf(number.longValue());
        }
        throw new IllegalArgumentException(claimName + " claim is missing in JWT");
    }

    private Long getClaimAsLong(String token, String claimName) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return getClaimAsLong(jwt, claimName);
        } catch (JwtException ex) {
            throw new IllegalArgumentException("Invalid JWT token", ex);
        }
    }

    private Long getClaimAsLong(Jwt jwt, String claimName) {
        Object claimValue = jwt.getClaims().get(claimName);
        if (claimValue instanceof Number number) {
            return number.longValue();
        }
        if (claimValue instanceof String stringValue && StringUtils.hasText(stringValue)) {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(claimName + " claim must be numeric", ex);
            }
        }
        throw new IllegalArgumentException(claimName + " claim is missing in JWT");
    }

    private String resolveToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");
        }
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }
}
