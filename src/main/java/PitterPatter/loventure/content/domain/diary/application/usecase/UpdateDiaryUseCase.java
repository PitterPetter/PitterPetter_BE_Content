package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.request.UpdateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.domain.entity.Content;
import PitterPatter.loventure.content.domain.diary.service.ContentService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDiaryUseCase {

    private final ContentService contentService;

    @Transactional
    public DiaryResponse execute(Long diaryId, Long userId, Long coupleId, UpdateDiaryRequest request) {
        Content diary = contentService.findByDiaryId(diaryId);

        // 사용자 권한 검증
        if(!diary.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.DIARY401, "다이어리 수정 권한이 없습니다. 다른 사용자의 다이어리는 수정할 수 없습니다.");
        }
        
        // 커플 정보 검증
        if(!diary.getCoupleId().equals(coupleId)) {
            throw new CustomException(ErrorCode.DIARY402, "커플 정보가 일치하지 않습니다. 해당 커플의 다이어리가 아닙니다.");
        }

        return contentService.updateDiary(diary, request.title(), request.content());
    }
}
