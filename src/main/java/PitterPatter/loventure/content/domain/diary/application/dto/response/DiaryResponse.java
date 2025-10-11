package PitterPatter.loventure.content.domain.diary.application.dto.response;

import PitterPatter.loventure.content.domain.comment.application.dto.response.CommentResponse;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.image.application.dto.response.ImageUploadResponse;
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
        List<CommentResponse> comments,
        
        // 이미지 관련 필드
        @Schema(description = "이미지 ID (있을 경우)")
        Long imageId,
        @Schema(description = "이미지 다운로드 URL (UPLOADED 상태인 경우)")
        String imageUrl,
        @Schema(description = "이미지 상태 (PENDING, UPLOADED, FAILED)")
        String imageStatus,
        @Schema(description = "이미지 URL 만료 시간 (초)")
        Integer imageExpiresIn,
        
        // 이미지 업로드 정보 (생성/수정 시)
        @Schema(description = "이미지 업로드 정보 (생성/수정 시)")
        ImageUploadResponse imageUpload
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
                .imageId(diary.getImage() != null ? diary.getImage().getId() : null)
                .imageStatus(diary.getImage() != null ? diary.getImage().getStatus().name() : null)
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
                .imageId(diary.getImage() != null ? diary.getImage().getId() : null)
                .imageStatus(diary.getImage() != null ? diary.getImage().getStatus().name() : null)
                .build();
    }
    
    /**
     * 이미지 업로드 정보와 함께 응답 생성 (생성/수정 시)
     */
    public static DiaryResponse createWithImageUpload(Diary diary, ImageUploadResponse imageUpload) {
        return builder()
                .contentId(diary.getDiaryId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .courseId(diary.getCourseId())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .author(diary.getAuthorName())
                .comments(List.of())
                .imageId(diary.getImage() != null ? diary.getImage().getId() : null)
                .imageStatus(diary.getImage() != null ? diary.getImage().getStatus().name() : null)
                .imageUpload(imageUpload)
                .build();
    }
    
    /**
     * 이미지 다운로드 URL과 함께 응답 생성 (조회 시)
     */
    public static DiaryResponse createWithImageUrl(Diary diary, String imageUrl, List<CommentResponse> comments) {
        return builder()
                .contentId(diary.getDiaryId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .courseId(diary.getCourseId())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .author(diary.getAuthorName())
                .comments(comments)
                .imageId(diary.getImage() != null ? diary.getImage().getId() : null)
                .imageUrl(imageUrl)
                .imageStatus(diary.getImage() != null ? diary.getImage().getStatus().name() : null)
                .imageExpiresIn(imageUrl != null ? 3600 : null) // 1시간
                .build();
    }
}
