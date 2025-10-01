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
        // 다이어리 엔터티 받기
        Diary diary = contentService.findByDiaryId(diaryId);

        // 다이어리 작성자와 커플이 요청자와 일치하는지 유효성 검사
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
