package PitterPatter.loventure.content.domain.image.ui;

import PitterPatter.loventure.content.domain.image.application.service.ImageService;
import PitterPatter.loventure.content.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Tag(name = "Image", description = "이미지 관리 API")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{imageId}/url")
    @Operation(summary = "이미지 Signed URL 조회", description = "이미지 ID로 Signed URL을 조회합니다 (15분 유효)")
    public BaseResponse<String> getImageUrl(
            @Parameter(description = "이미지 ID") @PathVariable Long imageId
    ) {
        String signedUrl = imageService.getSignedUrl(imageId);
        return BaseResponse.success(signedUrl);
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "이미지 삭제", description = "이미지를 삭제합니다 (소프트 삭제)")
    public BaseResponse<Void> deleteImage(
            @Parameter(description = "이미지 ID") @PathVariable Long imageId
    ) {
        imageService.deleteImage(imageId);
        return BaseResponse.success(null);
    }
}

