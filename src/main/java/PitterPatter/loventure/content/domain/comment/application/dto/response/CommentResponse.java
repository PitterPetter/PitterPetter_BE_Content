package PitterPatter.loventure.content.domain.comment.application.dto.response;

import PitterPatter.loventure.content.domain.comment.domain.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "댓글 응답 (다이어리 상세 조회 시)")
@Builder
public record CommentResponse(
        @Schema(description = "댓글 ID", example = "1")
        Long commentId,
        @Schema(description = "댓글 내용", example = "좋은 글이네요!")
        String content,
        @Schema(description = "작성자 ID", example = "123")
        Long userId,
        @Schema(description = "작성자 이름", example = "홍길동")
        String authorName,
        @Schema(description = "생성일시")
        LocalDateTime createdAt,
        @Schema(description = "수정일시")
        LocalDateTime updatedAt
) {
    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .authorName(comment.getAuthorName())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

