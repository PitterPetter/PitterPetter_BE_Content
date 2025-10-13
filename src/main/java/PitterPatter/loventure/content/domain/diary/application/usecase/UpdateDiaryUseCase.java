package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.request.UpdateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.DiaryService;
import PitterPatter.loventure.content.domain.image.application.dto.response.ImageUploadResponse;
import PitterPatter.loventure.content.domain.image.domain.entity.ImageType;
import PitterPatter.loventure.content.domain.image.domain.entity.Image;
import PitterPatter.loventure.content.domain.image.domain.repository.ImageRepository;
import PitterPatter.loventure.content.domain.image.service.ImageService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateDiaryUseCase {

    private final DiaryService diaryService;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    /**
     * 다이어리 수정을 실행합니다.
     * 
     * @param diaryId 수정할 다이어리 ID
     * @param userId 요청한 사용자 ID
     * @param coupleId 요청한 사용자의 커플 ID
     * @param request 수정 요청 정보 (제목, 내용, 이미지)
     * @return 수정된 다이어리 정보
     * @throws CustomException 권한 검증 실패 시
     */
    @Transactional
    public DiaryResponse execute(Long diaryId, Long userId, Long coupleId, UpdateDiaryRequest request) {
        log.info("Updating diary: diaryId={}, userId={}, hasImage={}, removeImage={}", 
            diaryId, userId, request.image() != null, request.removeImage());
        
        // 다이어리 조회
        Diary diary = diaryService.findByDiaryId(diaryId);

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

        // 다이어리 내용 수정
        DiaryResponse response = diaryService.updateDiary(diary, request.title(), request.content());
        
        // 이미지 처리
        ImageUploadResponse imageUpload = null;
        
        // 이미지 삭제 요청
        if (Boolean.TRUE.equals(request.removeImage())) {
            if (diary.getImage() != null) {
                log.info("Removing image from diary: diaryId={}, imageId={}", 
                    diaryId, diary.getImage().getId());
                Long imageIdToDelete = diary.getImage().getId();
                diary.removeImage();
                imageService.deleteImage(imageIdToDelete);
            }
        }
        // 이미지 교체 요청
        else if (request.image() != null) {
            // 기존 이미지 삭제 (있으면)
            if (diary.getImage() != null) {
                log.info("Replacing image for diary: diaryId={}, oldImageId={}", 
                    diaryId, diary.getImage().getId());
                Long oldImageId = diary.getImage().getId();
                diary.removeImage();
                imageService.deleteImage(oldImageId);
            }
            
            try {
                // 새 이미지 생성
                imageUpload = imageService.createImageMetadata(
                    request.image(),
                    ImageType.DIARY,
                    diaryId
                );
                
                Image newImage = imageRepository.findById(imageUpload.imageId())
                    .orElseThrow();
                diary.updateImage(newImage);
                
                log.info("New image metadata created: diaryId={}, imageId={}", 
                    diaryId, imageUpload.imageId());
                
            } catch (Exception e) {
                log.error("Failed to create new image metadata for diary: diaryId={}", diaryId, e);
                throw e;
            }
        }
        
        // 이미지 업로드 정보가 있으면 포함해서 응답
        if (imageUpload != null) {
            return DiaryResponse.createWithImageUpload(diary, imageUpload);
        }
        
        return DiaryResponse.create(diary);
    }
}
