package PitterPatter.loventure.content.domain.diary.application.dto.response;

import lombok.Builder;

@Builder
public record Author(
        Long userId,
        String nickname
) {
}
