package PitterPatter.loventure.content.domain.diary.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "작성자 정보")
@Builder
public record Author(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,
        @Schema(description = "닉네임", example = "사용자닉네임")
        String nickname
) {
}
