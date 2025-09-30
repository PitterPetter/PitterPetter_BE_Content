package PitterPatter.loventure.content.domain.diary.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "다이어리 수정 요청")
public record UpdateDiaryRequest(
        @Schema(description = "다이어리 제목", example = "오늘의 데이트")
        @Size(min = 1, max = 100)
        @NotBlank String title,
        @Schema(description = "다이어리 내용", example = "정말 즐거운 하루였습니다!")
        @Size(min = 1, max = 1000)
        @NotBlank String content
) {
}
