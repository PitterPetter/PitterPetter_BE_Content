package PitterPatter.loventure.content.domain.diary.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "다이어리 요약 정보")
@Builder
public record DiarySummary(
        @Schema(description = "다이어리 ID", example = "d_123")
        Long diaryId,
        @Schema(description = "다이어리 제목", example = "제주도 첫 여행")
        String title,
        @Schema(description = "다이어리 요약", example = "드디어 함께 떠난 첫 여행…")
        String excerpt,
        @Schema(description = "수정일시", example = "2025-09-05T00:00:00Z")
        LocalDateTime updatedAt,
        @Schema(description = "좋아요 수", example = "8")
        int likeCount
) {
}
