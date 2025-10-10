package PitterPatter.loventure.content.domain.diary.application.service;

import PitterPatter.loventure.content.domain.image.domain.entity.ImageType;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final Storage storage;

    @Value("${gcs.bucket-name}")
    private String bucketName;

    /**
     * 이미지를 GCS에 업로드하고 파일명을 반환합니다.
     *
     * @param file 업로드할 이미지 파일
     * @param imageType 이미지 타입 (DIARY, PROFILE 등)
     * @param referenceId 참조 ID (다이어리 ID 등)
     * @return 저장된 파일명 (예: "images/diary-123-uuid.jpg")
     */
    public String uploadImage(MultipartFile file, ImageType imageType, Long referenceId) throws IOException {
        // 원본 파일명에서 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 고유한 파일명 생성: images/{type}-{referenceId}-{uuid}.{extension}
        // GCS에서는 / 로 폴더처럼 구조화할 수 있음
        String typePrefix = imageType.name().toLowerCase();
        String fileName = String.format("images/%s-%d-%s%s", 
                typePrefix,
                referenceId != null ? referenceId : 0,
                UUID.randomUUID().toString(), 
                extension);

        log.info("Uploading image to GCS: bucket={}, fileName={}, type={}", bucketName, fileName, imageType);

        // GCS에 업로드
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        log.info("Image uploaded successfully: {}", fileName);
        return fileName;
    }

    /**
     * Signed URL을 생성합니다 (임시 접근 URL, 15분 유효)
     *
     * @param fileName GCS에 저장된 파일명
     * @return Signed URL
     */
    public String generateSignedUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);

        if (blob == null) {
            log.warn("File not found in GCS: {}", fileName);
            return null;
        }

        // 15분간 유효한 Signed URL 생성
        URL signedUrl = blob.signUrl(15, TimeUnit.MINUTES);
        
        log.debug("Generated signed URL for file: {}", fileName);
        return signedUrl.toString();
    }

    /**
     * GCS에서 이미지를 삭제합니다.
     *
     * @param fileName 삭제할 파일명
     * @return 삭제 성공 여부
     */
    public boolean deleteImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        BlobId blobId = BlobId.of(bucketName, fileName);
        boolean deleted = storage.delete(blobId);

        if (deleted) {
            log.info("Image deleted from GCS: {}", fileName);
        } else {
            log.warn("Failed to delete image or file not found: {}", fileName);
        }

        return deleted;
    }

    /**
     * 파일 존재 여부를 확인합니다.
     *
     * @param fileName 확인할 파일명
     * @return 존재 여부
     */
    public boolean exists(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);
        return blob != null && blob.exists();
    }
}

