package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.ContentService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadDiaryUseCase {

    private final ContentService contentService;

    public DiaryResponse execute(Long userId, Long coupleId, Long diaryId) {
        Diary diary = contentService.findByDiaryId(diaryId);

        // 다이어리 작성자랑 요청자랑 다르면 익셉션
        if(!diary.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.DIARY401);
        }
        //다이어 작성 커플과 요청 커플과 다르면 익셉션
        if(!diary.getCoupleId().equals(coupleId)) {
            throw new CustomException(ErrorCode.DIARY402);
        }

        return DiaryResponse.create(diary);
    }
}
