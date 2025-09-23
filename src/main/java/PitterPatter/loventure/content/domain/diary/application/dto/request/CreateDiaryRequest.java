package PitterPatter.loventure.content.domain.diary.application.dto.request;

public record CreateDiaryRequest(
        Long course_id,
        String title,
        String content
) {

}
