package PitterPatter.loventure.content.domain.diary.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "다이어리 목록 응답")
@Builder
public record DiaryListResponse(
        @Schema(description = "다이어리 목록")
        List<DiarySummary> content,
        @Schema(description = "페이지 정보")
        PageInfo page
) {
}
