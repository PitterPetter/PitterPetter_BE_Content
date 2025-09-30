package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.ContentService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDiaryUseCase {

    private final ContentService contentService;

    @Transactional
    public Void execute(Long userId, Long coupleId, Long diaryId) {
        Diary diary = contentService.findByDiaryId(diaryId);
        if(!diary.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.DIARY401);
        }
        if(!diary.getCoupleId().equals(coupleId)) {
            throw new CustomException(ErrorCode.DIARY402);
        }

        contentService.deleteDiary(diary);
        return null;
    }
}
