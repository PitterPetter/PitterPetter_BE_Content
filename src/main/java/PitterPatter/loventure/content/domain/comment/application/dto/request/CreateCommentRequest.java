package PitterPatter.loventure.content.domain.comment.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "댓글 생성 요청")
public record CreateCommentRequest(
        @Schema(description = "댓글 내용", example = "좋은 글이네요!")
        @NotBlank(message = "댓글 내용은 필수입니다")
        String content
) {
}

