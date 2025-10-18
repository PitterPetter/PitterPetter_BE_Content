package PitterPatter.loventure.content.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CORS 관련 요청을 로깅하는 필터
 * 
 * 모든 요청의 Origin, 메서드, 헤더 등을 로그로 남겨 CORS 문제를 디버깅할 수 있도록 합니다.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String origin = httpRequest.getHeader("Origin");
        String userAgent = httpRequest.getHeader("User-Agent");
        String referer = httpRequest.getHeader("Referer");
        String contentType = httpRequest.getHeader("Content-Type");
        String authorization = httpRequest.getHeader("Authorization");
        String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");
        String remoteAddr = httpRequest.getRemoteAddr();
        
        // PATCH 요청이나 OPTIONS 요청에 대해서만 상세 로그 출력
        if ("PATCH".equals(method) || "OPTIONS".equals(method) || uri.contains("/api/images/")) {
            log.info("=== CORS 요청 로그 ===");
            log.info("메서드: {}", method);
            log.info("URI: {}", uri);
            log.info("Origin: {}", origin);
            log.info("User-Agent: {}", userAgent);
            log.info("Referer: {}", referer);
            log.info("Content-Type: {}", contentType);
            try {
                log.info("Authorization: {}", authorization != null ? "Bearer " + authorization.substring(0, Math.min(20, authorization.length())) + "..." : "null");
            } catch (Exception e) {
                log.info("Authorization: {}", authorization);
            }
            log.info("X-Forwarded-For: {}", xForwardedFor);
            log.info("Remote Address: {}", remoteAddr);
            log.info("======================");
        }
        
        // CORS 헤더 추가 (디버깅용)
        if (origin != null) {
            // Origin이 허용된 도메인인지 확인
            boolean isAllowedOrigin = isAllowedOrigin(origin);
            log.info("Origin 허용 여부: {} - {}", origin, isAllowedOrigin ? "허용" : "차단");
            
            if (isAllowedOrigin) {
                httpResponse.setHeader("Access-Control-Allow-Origin", origin);
                httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
                httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
                httpResponse.setHeader("Access-Control-Expose-Headers", "X-User-Id, X-Couple-Id");
                httpResponse.setHeader("Access-Control-Max-Age", "3600");
            }
        }
        
        // OPTIONS 요청 처리
        if ("OPTIONS".equals(method)) {
            log.info("OPTIONS preflight 요청 처리: {}", uri);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(request, response);
        
        // 응답 후 로그
        if ("PATCH".equals(method) || uri.contains("/api/images/")) {
            log.info("=== CORS 응답 로그 ===");
            log.info("상태 코드: {}", httpResponse.getStatus());
            log.info("응답 헤더 - Access-Control-Allow-Origin: {}", httpResponse.getHeader("Access-Control-Allow-Origin"));
            log.info("응답 헤더 - Access-Control-Allow-Methods: {}", httpResponse.getHeader("Access-Control-Allow-Methods"));
            log.info("응답 헤더 - Access-Control-Allow-Headers: {}", httpResponse.getHeader("Access-Control-Allow-Headers"));
            log.info("======================");
        }
    }
    
    private boolean isAllowedOrigin(String origin) {
        return origin != null && (
            origin.equals("https://loventure.us") ||
            origin.startsWith("https://") && origin.endsWith(".loventure.us") ||
            origin.startsWith("http://localhost:") ||
            origin.startsWith("http://127.0.0.1:")
        );
    }
}
