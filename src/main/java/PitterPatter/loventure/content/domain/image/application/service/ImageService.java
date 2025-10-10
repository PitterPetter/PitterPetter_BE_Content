package PitterPatter.loventure.content.domain.image.application.service;

import PitterPatter.loventure.content.domain.diary.application.service.ImageStorageService;
import PitterPatter.loventure.content.domain.image.domain.entity.Image;
import PitterPatter.loventure.content.domain.image.domain.entity.ImageType;
import PitterPatter.loventure.content.domain.image.domain.repository.ImageRepository;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageStorageService imageStorageService;

    /**
     * 이미지를 GCS에 업로드하고 Image 엔티티를 생성합니다.
     *
     * @param file 업로드할 파일
     * @param imageType 이미지 타입
     * @param referenceId 참조 ID (다이어리 ID 등)
     * @return 생성된 Image 엔티티
     */
    public Image uploadImage(MultipartFile file, ImageType imageType, Long referenceId) {
        try {
            // GCS에 업로드
            String fileName = imageStorageService.uploadImage(file, imageType, referenceId);

            // Image 엔티티 생성
            Image image = Image.builder()
                    .fileName(fileName)
                    .originalFileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .imageType(imageType)
                    .referenceId(referenceId)
                    .isDeleted(false)
                    .build();

            return imageRepository.save(image);
        } catch (IOException e) {
            log.error("Failed to upload image", e);
            throw new CustomException(ErrorCode.IMAGE400);
        }
    }

    /**
     * 이미지 ID로 Signed URL을 생성합니다.
     *
     * @param imageId 이미지 ID
     * @return Signed URL
     */
    @Transactional(readOnly = true)
    public String getSignedUrl(Long imageId) {
        Image image = imageRepository.findByImageIdAndIsDeletedFalse(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE404));

        return imageStorageService.generateSignedUrl(image.getFileName());
    }

    /**
     * 이미지를 소프트 삭제하고 GCS에서도 삭제합니다.
     *
     * @param imageId 이미지 ID
     */
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findByImageIdAndIsDeletedFalse(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE404));

        // GCS에서 삭제
        boolean deleted = imageStorageService.deleteImage(image.getFileName());
        
        if (deleted) {
            // DB에서 소프트 삭제
            image.delete();
            log.info("Image soft deleted: imageId={}, fileName={}", imageId, image.getFileName());
        } else {
            log.warn("Failed to delete image from GCS, but marking as deleted: imageId={}", imageId);
            image.delete();
        }
    }

    /**
     * 참조 ID로 이미지를 조회합니다.
     *
     * @param referenceId 참조 ID (다이어리 ID 등)
     * @param imageType 이미지 타입
     * @return Image 엔티티 (Optional)
     */
    @Transactional(readOnly = true)
    public Image findByReferenceId(Long referenceId, ImageType imageType) {
        return imageRepository.findByReferenceIdAndImageTypeAndIsDeletedFalse(referenceId, imageType)
                .orElse(null);
    }

    /**
     * 기존 이미지를 삭제하고 새 이미지로 교체합니다.
     *
     * @param existingImageId 기존 이미지 ID
     * @param newFile 새 파일
     * @param imageType 이미지 타입
     * @param referenceId 참조 ID
     * @return 새로 생성된 Image 엔티티
     */
    public Image replaceImage(Long existingImageId, MultipartFile newFile, ImageType imageType, Long referenceId) {
        // 기존 이미지 삭제
        if (existingImageId != null) {
            deleteImage(existingImageId);
        }

        // 새 이미지 업로드
        return uploadImage(newFile, imageType, referenceId);
    }
}

