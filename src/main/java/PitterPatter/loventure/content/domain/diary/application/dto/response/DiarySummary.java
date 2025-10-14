package PitterPatter.loventure.content.domain.diary.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "다이어리 요약 정보")
@Builder
public record DiarySummary(
        @Schema(description = "다이어리 ID", example = "1234567890123456789")
        String diaryId,
        @Schema(description = "다이어리 제목", example = "제주도 첫 여행")
        String title,
        @Schema(description = "다이어리 요약", example = "드디어 함께 떠난 첫 여행…")
        String excerpt,
        @Schema(description = "수정일시", example = "2025-09-05T00:00:00Z")
        LocalDateTime updatedAt,
        @Schema(description = "좋아요 수", example = "8")
        int likeCount,
        
        // 이미지 관련 필드
        @Schema(description = "이미지 ID (있을 경우)")
        String imageId,
        @Schema(description = "이미지 다운로드 URL (UPLOADED 상태인 경우)")
        String imageUrl,
        @Schema(description = "이미지 상태 (PENDING, UPLOADED, FAILED)")
        String imageStatus,
        @Schema(description = "이미지 URL 만료 시간 (초)")
        Integer imageExpiresIn
) {
}
