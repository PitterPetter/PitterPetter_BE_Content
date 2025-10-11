package PitterPatter.loventure.content.domain.image.application.usecase;

import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.domain.repository.DiaryRepository;
import PitterPatter.loventure.content.domain.image.domain.entity.Image;
import PitterPatter.loventure.content.domain.image.service.ImageService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이미지 삭제 UseCase
 * 
 * 다이어리에서 이미지만 삭제하고 싶을 때 사용합니다.
 * 다이어리 작성자만 삭제할 수 있습니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteImageUseCase {
    
    private final ImageService imageService;
    private final DiaryRepository diaryRepository;
    
    @Transactional
    public void execute(Long imageId, Long userId, Long coupleId) {
        log.info("Delete image request: imageId={}, userId={}, coupleId={}", imageId, userId, coupleId);
        
        // 1. 이미지 조회
        Image image = imageService.getImage(imageId);
        
        // 2. 연관된 다이어리 조회 및 권한 체크
        Diary diary = diaryRepository.findById(image.getReferenceId())
            .orElseThrow(() -> new CustomException(ErrorCode.DIARY404));
        
        // 3. 작성자 확인
        if (!diary.getUserId().equals(userId)) {
            log.warn("Image delete access denied: imageId={}, userId={}, diaryUserId={}", 
                imageId, userId, diary.getUserId());
            throw new CustomException(ErrorCode.IMAGE_ACCESS_DENIED);
        }
        
        // 4. coupleId 확인
        if (!diary.getCoupleId().equals(coupleId)) {
            log.warn("Image delete access denied (couple mismatch): imageId={}, coupleId={}, diaryCoupleId={}", 
                imageId, coupleId, diary.getCoupleId());
            throw new CustomException(ErrorCode.DIARY402);
        }
        
        // 5. 다이어리에서 이미지 참조 제거 (FK 해제)
        diary.removeImage();
        diaryRepository.save(diary);
        
        // 6. 이미지 삭제 (GCS + DB)
        imageService.deleteImage(imageId);
        
        log.info("Image deleted successfully: imageId={}", imageId);
    }
}

