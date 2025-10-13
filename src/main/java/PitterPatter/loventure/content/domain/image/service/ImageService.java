package PitterPatter.loventure.content.domain.image.service;

import PitterPatter.loventure.content.domain.image.application.dto.request.ImageMetadataRequest;
import PitterPatter.loventure.content.domain.image.application.dto.response.ImageUploadResponse;
import PitterPatter.loventure.content.domain.image.domain.entity.ImageStatus;
import PitterPatter.loventure.content.domain.image.domain.entity.ImageType;
import PitterPatter.loventure.content.domain.image.domain.entity.Image;
import PitterPatter.loventure.content.domain.image.domain.repository.ImageRepository;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import PitterPatter.loventure.content.global.infra.gcs.GcsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 이미지 서비스
 * 
 * 이미지 메타데이터 관리 및 presignedURL 생성을 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    
    private final ImageRepository imageRepository;
    private final GcsService gcsService;
    
    // 허용 파일 타입
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
        "image/jpeg", "image/jpg", "image/png", 
        "image/gif", "image/webp", 
        "image/heic", "image/heif"
    );
    
    // 최대 파일 크기 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    // presignedURL 만료 시간
    private static final int UPLOAD_EXPIRATION_MINUTES = 5;
    private static final int DOWNLOAD_EXPIRATION_MINUTES = 60;
    
    /**
     * 이미지 메타데이터 생성 및 업로드 URL 반환
     * 
     * @param request 이미지 메타데이터 요청
     * @param imageType 이미지 타입
     * @param referenceId 참조 ID (다이어리 ID 등)
     * @return ImageUploadResponse (imageId, presignedUrl, expiresIn)
     */
    public ImageUploadResponse createImageMetadata(
            ImageMetadataRequest request,
            ImageType imageType,
            Long referenceId
    ) {
        log.info("Creating image metadata: type={}, referenceId={}, fileName={}", 
            imageType, referenceId, request.originalFileName());
        
        // 1. 파일 검증
        validateImage(request);
        
        // 2. UUID 생성
        String uuid = UUID.randomUUID().toString();
        
        // 3. 파일 확장자 추출
        String extension = extractExtension(request.originalFileName());
        
        // 4. GCS 경로 생성: images/diary/{uuid}.{ext}
        String objectPath = String.format(
            "images/%s/%s.%s",
            imageType.name().toLowerCase(),
            uuid,
            extension
        );
        
        // 5. Image 엔터티 생성
        Image image = Image.builder()
            .uuid(uuid)
            .objectPath(objectPath)
            .imageType(imageType)
            .referenceId(referenceId)
            .status(ImageStatus.PENDING)
            .contentType(request.contentType())
            .sizeBytes(request.sizeBytes())
            .originalFileName(request.originalFileName())
            .build();
        
        imageRepository.save(image);
        
        log.info("Image metadata saved: imageId={}, objectPath={}", image.getImageId(), objectPath);
        
        // 6. Presigned URL 생성 (5분)
        String presignedUrl = gcsService.generateUploadUrl(
            objectPath,
            request.contentType(),
            UPLOAD_EXPIRATION_MINUTES
        );
        
        return ImageUploadResponse.of(
            image.getImageId(),
            presignedUrl, 
            UPLOAD_EXPIRATION_MINUTES * 60
        );
    }
    
    /**
     * 업로드 완료 처리
     * 
     * @param imageId 이미지 ID
     */
    public void markAsUploaded(Long imageId) {
        log.info("Marking image as uploaded: imageId={}", imageId);
        
        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        
        image.markAsUploaded();
        
        log.info("Image upload completed: imageId={}, objectPath={}", imageId, image.getObjectPath());
    }
    
    /**
     * 업로드 실패 처리
     * 
     * @param imageId 이미지 ID
     */
    public void markAsFailed(Long imageId) {
        log.info("Marking image as failed: imageId={}", imageId);
        
        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        
        image.markAsFailed();
        
        log.warn("Image upload failed: imageId={}, objectPath={}", imageId, image.getObjectPath());
    }
    
    /**
     * 이미지 삭제 (GCS + DB)
     * 
     * @param imageId 이미지 ID
     */
    public void deleteImage(Long imageId) {
        log.info("Deleting image: imageId={}", imageId);
        
        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        
        // GCS에서 삭제
        gcsService.deleteImage(image.getObjectPath());
        
        // DB에서 삭제
        imageRepository.delete(image);
        
        log.info("Image deleted successfully: imageId={}, objectPath={}", imageId, image.getObjectPath());
    }
    
    /**
     * 다운로드 URL 생성
     * 
     * @param imageId 이미지 ID
     * @return presignedURL (UPLOADED 상태인 경우만)
     */
    public String generateDownloadUrl(Long imageId) {
        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        
        // UPLOADED 상태인 경우만 URL 생성
        if (image.getStatus() != ImageStatus.UPLOADED) {
            log.warn("Image is not ready for download: imageId={}, status={}", imageId, image.getStatus());
            return null;
        }
        
        return gcsService.generateDownloadUrl(
            image.getObjectPath(),
            DOWNLOAD_EXPIRATION_MINUTES
        );
    }
    
    /**
     * 이미지 엔터티 반환
     * 
     * @param imageId 이미지 ID
     * @return Image 엔터티
     */
    public Image getImage(Long imageId) {
        return imageRepository.findById(imageId)
            .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
    }
    
    /**
     * 파일 검증
     */
    private void validateImage(ImageMetadataRequest request) {
        // 파일 타입 검증
        String contentType = request.contentType().toLowerCase();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.warn("Invalid image type: {}", contentType);
            throw new CustomException(ErrorCode.INVALID_IMAGE_TYPE, 
                "지원하지 않는 형식: " + contentType);
        }
        
        // 파일 크기 검증 (5MB)
        if (request.sizeBytes() > MAX_FILE_SIZE) {
            log.warn("Image size exceeded: {} bytes (max: {} bytes)", request.sizeBytes(), MAX_FILE_SIZE);
            throw new CustomException(ErrorCode.IMAGE_SIZE_EXCEEDED,
                String.format("파일 크기: %.2f MB (최대 5MB)", request.sizeBytes() / 1024.0 / 1024.0));
        }
        
        log.debug("Image validation passed: contentType={}, size={} bytes", contentType, request.sizeBytes());
    }
    
    /**
     * 파일 확장자 추출
     */
    private String extractExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            log.warn("Invalid file name: {}", fileName);
            throw new CustomException(ErrorCode.INVALID_IMAGE_TYPE, 
                "잘못된 파일명: " + fileName);
        }
        return fileName.substring(lastDot + 1).toLowerCase();
    }
}

