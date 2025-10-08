package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.application.service.UserLookupService;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDiaryUseCase {

    private final ContentService contentService;
    private final UserLookupService userLookupService;

    @Transactional
    public DiaryResponse execute(String token, Long userId, Long coupleId, CreateDiaryRequest request) {

        String author = userLookupService.getUserName(userId, token);

        // Diary entity 생성
        Diary diary = contentService.saveDiary(userId, author, coupleId, request);

        return DiaryResponse.create(diary);
    }
}
