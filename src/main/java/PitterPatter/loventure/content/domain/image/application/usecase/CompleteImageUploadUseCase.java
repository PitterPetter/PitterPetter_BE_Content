package PitterPatter.loventure.content.domain.image.application.usecase;

import PitterPatter.loventure.content.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이미지 업로드 완료 처리 UseCase
 * 
 * 프론트엔드가 GCS에 이미지를 성공적으로 업로드한 후 호출하여
 * 이미지 상태를 UPLOADED로 변경합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompleteImageUploadUseCase {
    
    private final ImageService imageService;
    
    @Transactional
    public void execute(String imageId) {
        log.info("Image upload complete callback: imageId={}", imageId);
        
        imageService.markAsUploaded(imageId);
        
        log.info("Image upload completed successfully: imageId={}", imageId);
    }
}

