package PitterPatter.loventure.content.global.infra;


import PitterPatter.loventure.content.domain.diary.application.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;

@FeignClient(
        name = "authClient",
        url = "http://loventure-prod-auth-service.loventure-app.svc.cluster.local"
)
public interface AuthClient {

    @GetMapping("/internal/users/{userId}")
    UserProfileResponse getUserById(
        @PathVariable("userId") Long userId,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    );

}
