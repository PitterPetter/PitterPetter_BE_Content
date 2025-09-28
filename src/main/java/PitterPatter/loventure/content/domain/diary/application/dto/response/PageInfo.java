package PitterPatter.loventure.content.domain.diary.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "페이지 정보")
@Builder
public record PageInfo(
        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        int page,
        @Schema(description = "페이지 크기", example = "12")
        int size,
        @Schema(description = "전체 요소 수", example = "34")
        long totalElements,
        @Schema(description = "전체 페이지 수", example = "3")
        int totalPages
) {
}
