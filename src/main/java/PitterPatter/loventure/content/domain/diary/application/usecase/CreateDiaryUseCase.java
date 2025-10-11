package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.application.service.UserLookupService;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.DiaryServiec;
import PitterPatter.loventure.content.domain.image.application.dto.response.ImageUploadResponse;
import PitterPatter.loventure.content.domain.image.domain.ImageType;
import PitterPatter.loventure.content.domain.image.domain.entity.Image;
import PitterPatter.loventure.content.domain.image.domain.repository.ImageRepository;
import PitterPatter.loventure.content.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDiaryUseCase {

    private final DiaryServiec diaryServiec;
    private final UserLookupService userLookupService;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    @Transactional
    public DiaryResponse execute(String token, Long userId, Long coupleId, CreateDiaryRequest request) {
        log.info("Creating diary: userId={}, coupleId={}, hasImage={}", 
            userId, coupleId, request.image() != null);

        String author = userLookupService.getUserName(userId);

        // 1. Diary entity 생성 (이미지 없이)
        Diary diary = diaryServiec.saveDiary(userId, author, coupleId, request);

        // 2. 이미지 처리
        ImageUploadResponse imageUpload = null;
        if (request.image() != null) {
            log.info("Processing image for diary: diaryId={}", diary.getDiaryId());
            
            try {
                // 2-1. Image 메타데이터 생성 및 presignedURL 발급
                imageUpload = imageService.createImageMetadata(
                    request.image(),
                    ImageType.DIARY,
                    diary.getDiaryId()
                );
                
                // 2-2. Diary에 Image 연결
                Image image = imageRepository.findById(imageUpload.imageId())
                    .orElseThrow();
                diary.updateImage(image);
                
                log.info("Image metadata created: diaryId={}, imageId={}", 
                    diary.getDiaryId(), imageUpload.imageId());
                
            } catch (Exception e) {
                log.error("Failed to create image metadata for diary: diaryId={}", 
                    diary.getDiaryId(), e);
                // presignedURL 생성 실패 시 트랜잭션 롤백
                throw e;
            }
        }

        return DiaryResponse.createWithImageUpload(diary, imageUpload);
    }
}
