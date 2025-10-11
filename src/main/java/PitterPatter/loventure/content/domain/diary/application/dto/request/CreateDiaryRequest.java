package PitterPatter.loventure.content.domain.diary.application.dto.request;

import PitterPatter.loventure.content.domain.image.application.dto.request.ImageMetadataRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Schema(description = "다이어리 생성 요청")
public record CreateDiaryRequest(
        @Schema(description = "코스 ID", example = "1")
        @NotNull Long courseId,

        @Schema(description = "다이어리 제목", example = "오늘의 데이트")
        @Size(min = 1, max = 100)
        @NotBlank String title,

        @Schema(description = "다이어리 내용", example = "정말 즐거운 하루였습니다!")
        @Size(min = 1, max = 1000)
        @NotBlank String content,

        @DecimalMax(value = "5.0", message = "평점은 5.0 이하")
        @DecimalMin(value = "0.0", message = "평점은 0.0 이상")
        @Schema(description = "평점", example = "4.5")
        double rating,

        @Schema(description = "이미지 메타데이터 (선택)")
        @Valid
        ImageMetadataRequest image
) {

}