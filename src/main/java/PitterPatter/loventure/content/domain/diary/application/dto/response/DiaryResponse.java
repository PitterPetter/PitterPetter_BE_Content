package PitterPatter.loventure.content.domain.diary.application.dto.response;

import PitterPatter.loventure.content.domain.comment.application.dto.response.CommentResponse;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "다이어리 응답")
@Builder
public record DiaryResponse(
        @Schema(description = "콘텐츠 ID", example = "1")
        Long contentId,
        @Schema(description = "다이어리 제목", example = "오늘의 데이트")
        String title,
        @Schema(description = "다이어리 내용", example = "정말 즐거운 하루였습니다!")
        String content,
        @Schema(description = "코스 ID", example = "1")
        Long courseId,
        @Schema(description = "작성자 userId")
        String userId,
        @Schema(description = "작성자 정보")
        String author,
        @Schema(description = "생성일시", example = "2025-09-25T02:42:50.798317")
        LocalDateTime createdAt,
        @Schema(description = "수정일시", example = "2025-09-25T02:42:50.798317")
        LocalDateTime updatedAt,
        @Schema(description = "댓글 목록")
        List<CommentResponse> comments
) {
    public static DiaryResponse create(Diary diary) {
        return builder()
                .contentId(diary.getDiaryId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .courseId(diary.getCourseId())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .author(diary.getAuthorName())
                .comments(List.of())
                .build();
    }

    public static DiaryResponse createWithComments(Diary diary, List<CommentResponse> comments) {
        return builder()
                .contentId(diary.getDiaryId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .courseId(diary.getCourseId())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .author(diary.getAuthorName())
                .comments(comments)
                .build();
    }
}
