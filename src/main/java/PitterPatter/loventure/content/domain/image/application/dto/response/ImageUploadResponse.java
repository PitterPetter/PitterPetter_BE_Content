package PitterPatter.loventure.content.domain.image.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 이미지 업로드 응답 DTO
 * 
 * presignedURL과 이미지 ID를 함께 반환
 */
@Schema(description = "이미지 업로드 응답")
public record ImageUploadResponse(
        
        @Schema(description = "이미지 ID", example = "1")
        Long imageId,
        
        @Schema(description = "업로드용 presignedURL")
        String presignedUrl,
        
        @Schema(description = "URL 만료 시간 (초)", example = "300")
        int expiresIn
) {
    
    public static ImageUploadResponse of(Long imageId, String presignedUrl, int expiresIn) {
        return new ImageUploadResponse(imageId, presignedUrl, expiresIn);
    }
}

