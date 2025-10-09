package PitterPatter.loventure.content.domain.comment.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "댓글 수정 요청")
public record UpdateCommentRequest(
        @Schema(description = "댓글 내용", example = "수정된 댓글입니다")
        @NotBlank(message = "댓글 내용은 필수입니다")
        String content
) {
}

