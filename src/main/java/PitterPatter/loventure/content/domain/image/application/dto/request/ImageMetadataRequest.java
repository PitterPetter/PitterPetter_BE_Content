package PitterPatter.loventure.content.domain.image.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 이미지 메타데이터 요청 DTO
 * 
 * 다이어리 생성/수정 시 이미지 정보를 함께 전달하기 위한 DTO
 */
@Schema(description = "이미지 메타데이터 요청")
public record ImageMetadataRequest(
        
        @Schema(description = "원본 파일명", example = "photo.jpg")
        @NotBlank(message = "파일명은 필수입니다")
        String originalFileName,
        
        @Schema(description = "파일 Content-Type", example = "image/jpeg")
        @NotBlank(message = "Content-Type은 필수입니다")
        String contentType,
        
        @Schema(description = "파일 크기 (bytes)", example = "2048000")
        @NotNull(message = "파일 크기는 필수입니다")
        @Positive(message = "파일 크기는 0보다 커야 합니다")
        Long sizeBytes
) {
}

