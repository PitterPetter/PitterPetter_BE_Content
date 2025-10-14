package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.comment.service.CommentService;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.DiaryService;
import PitterPatter.loventure.content.domain.image.service.ImageService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteDiaryUseCase {

    private final DiaryService diaryService;
    private final CommentService commentService;
    private final ImageService imageService;

    @Transactional
    public Void execute(String userId, String coupleId, String diaryId) {
        log.info("Deleting diary: diaryId={}, userId={}, coupleId={}", diaryId, userId, coupleId);
        
        // 다이어리 엔터티 받기
        Diary diary = diaryService.findByDiaryId(diaryId);

        // 다이어리 작성자와 커플이 요청자와 일치하는지 유효성 검사
        if(!diary.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.DIARY401);
        }
        if(!diary.getCoupleId().equals(coupleId)) {
            throw new CustomException(ErrorCode.DIARY402);
        }

        // 이미지 삭제 (GCS + DB)
        if (diary.getImage() != null) {
            log.info("Deleting image with diary: diaryId={}, imageId={}", 
                diaryId, diary.getImage().getImageId());
            String imageIdToDelete = diary.getImage().getImageId();
            diary.removeImage();  // FK 제거
            imageService.deleteImage(imageIdToDelete);
        }

        // 댓글 먼저 삭제
        commentService.deleteCommentsByDiaryId(diaryId);

        // 다이어리 삭제
        diaryService.deleteDiary(diary);
        
        log.info("Diary deleted successfully: diaryId={}", diaryId);
        return null;
    }
}