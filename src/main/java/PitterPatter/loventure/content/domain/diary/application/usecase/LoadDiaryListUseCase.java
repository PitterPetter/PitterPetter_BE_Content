package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryListResponse;
import PitterPatter.loventure.content.domain.diary.service.DiaryServiec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadDiaryListUseCase {

    private final DiaryServiec diaryServiec;

    public DiaryListResponse execute(Long userId, Long coupleId, int page, int size) {
        return diaryServiec.loadDiaryList(userId, coupleId, page, size);
    }
}
