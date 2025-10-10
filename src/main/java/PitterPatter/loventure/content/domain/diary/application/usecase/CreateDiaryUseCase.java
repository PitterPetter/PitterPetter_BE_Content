package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.application.service.UserLookupService;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.DiaryServiec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDiaryUseCase {

    private final DiaryServiec diaryServiec;
    private final UserLookupService userLookupService;

    @Transactional
    public DiaryResponse execute(Long userId, Long coupleId, CreateDiaryRequest request) {

        // userId로 사용자 이름 조회 (이미 JWT 인증 완료된 상태)
        String author = userLookupService.getUserName(userId);

        // Diary entity 생성
        Diary diary = diaryServiec.saveDiary(userId, author, coupleId, request);

        return DiaryResponse.create(diary);
    }
}
