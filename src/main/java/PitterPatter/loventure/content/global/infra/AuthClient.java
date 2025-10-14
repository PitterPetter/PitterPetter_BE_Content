package PitterPatter.loventure.content.global.infra;


import PitterPatter.loventure.content.domain.diary.application.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "authClient",
        //url = "${auth.service.url:http://localhost:8080}"  // 로컬: Mock, 배포: Config Server에서 K8s URL
        url = "http://loventure-prod-auth-service.loventure-app.svc.cluster.local:8081" // 배포
)
public interface AuthClient {

    /**
     * 내부 MSA 통신: userId로 사용자 이름 조회
     *
     * Authorization 헤더 불필요 (이미 Content 서비스에서 JWT 인증 완료)
     * 내부 서비스간 통신은 신뢰할 수 있으므로 userId만 전달
     */
    @GetMapping("/internal/user/{userId}")
    UserProfileResponse getUserById(
            @PathVariable("userId") Long userId
    );

}
