package PitterPatter.loventure.content.domain.diary.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "다이어리 생성 요청")
public record CreateDiaryRequest(
        @Schema(description = "코스 ID", example = "1")
        Long course_id,
        @Schema(description = "다이어리 제목", example = "오늘의 데이트")
        String title,
        @Schema(description = "다이어리 내용", example = "정말 즐거운 하루였습니다!")
        String content,
        @Schema(description = "평점", example = "4.5", minimum = "0.0", maximum = "5.0")
        double rating
) {

}