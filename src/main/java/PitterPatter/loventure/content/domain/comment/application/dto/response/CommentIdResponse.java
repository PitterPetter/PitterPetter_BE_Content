package PitterPatter.loventure.content.domain.comment.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "댓글 ID 응답 (작성/수정 시)")
@Builder
public record CommentIdResponse(
        @Schema(description = "댓글 ID", example = "1234567890123456789")
        String commentId
) {
    public static CommentIdResponse of(Long commentId) {
        return CommentIdResponse.builder()
                .commentId(String.valueOf(commentId))
                .build();
    }
}

