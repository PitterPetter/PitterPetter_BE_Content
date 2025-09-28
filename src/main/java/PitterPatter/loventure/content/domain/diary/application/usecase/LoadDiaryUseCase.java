package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadDiaryUseCase {

    private final ContentService contentService;

    public DiaryResponse execute(Long userId, Long coupleId, Long diaryId) {
        return contentService.loadDiary(coupleId, diaryId);
    }
}
