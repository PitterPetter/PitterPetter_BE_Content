package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.request.UpdateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.DiaryServiec;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 다이어리 수정을 처리하는 UseCase
 * 
 * 이 클래스는 다이어리 수정 요청을 처리하며, 다음과 같은 검증을 수행합니다:
 * 1. 사용자 권한 검증: 다이어리 작성자만 수정 가능
 * 2. 커플 정보 검증: 같은 커플의 다이어리만 수정 가능
 * 
 * 검증 실패 시 CustomException을 발생시켜 GlobalExceptionHandler에서 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class UpdateDiaryUseCase {

    private final DiaryServiec diaryServiec;

    /**
     * 다이어리 수정을 실행합니다.
     * 
     * @param diaryId 수정할 다이어리 ID
     * @param userId 요청한 사용자 ID
     * @param coupleId 요청한 사용자의 커플 ID
     * @param request 수정 요청 정보 (제목, 내용)
     * @return 수정된 다이어리 정보
     * @throws CustomException 권한 검증 실패 시
     */
    @Transactional
    public DiaryResponse execute(Long diaryId, Long userId, Long coupleId, UpdateDiaryRequest request) {
        // 다이어리 조회
        Diary diary = diaryServiec.findByDiaryId(diaryId);

        // 사용자 권한 검증: 다이어리 작성자만 수정 가능
        if(!diary.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.DIARY401, 
                "다이어리 수정 권한이 없습니다. 다른 사용자의 다이어리는 수정할 수 없습니다.");
        }
        
        // 커플 정보 검증: 같은 커플의 다이어리만 수정 가능
        if(!diary.getCoupleId().equals(coupleId)) {
            throw new CustomException(ErrorCode.DIARY402, 
                "커플 정보가 일치하지 않습니다. 해당 커플의 다이어리가 아닙니다.");
        }

        // 검증 통과 시 다이어리 수정 실행
        return diaryServiec.updateDiary(diary, request.title(), request.content());
    }
}
