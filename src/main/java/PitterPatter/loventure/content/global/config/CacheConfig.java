package PitterPatter.loventure.content.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Spring Cache 설정
 * 
 * 이미지 다운로드 URL 캐싱을 통해 GCS 비용을 절감합니다.
 */
@Slf4j
@Configuration
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        log.info("Initializing Caffeine Cache Manager for image download URLs");
        
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("imageDownloadUrls");
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(55, TimeUnit.MINUTES)  // 55분 후 자동 삭제 (presignedURL 60분이므로 여유 5분)
            .maximumSize(1000)  // 최대 1000개 URL 캐싱
            .recordStats()  // 캐시 통계 기록 (모니터링용)
        );
        
        log.info("Cache configured: expireAfterWrite=55min, maximumSize=1000");
        
        return cacheManager;
    }
}

