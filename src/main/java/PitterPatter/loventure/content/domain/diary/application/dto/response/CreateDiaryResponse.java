package PitterPatter.loventure.content.domain.diary.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateDiaryResponse(
        Long diaryId,
        String title,
        String content,
        Long courseId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int likeCount,
        boolean isLiked,
        Author author
) {
}
