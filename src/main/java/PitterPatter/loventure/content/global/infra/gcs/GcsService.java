package PitterPatter.loventure.content.global.infra.gcs;

import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Google Cloud Storage 서비스
 * 
 * presignedURL 생성 및 파일 삭제를 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GcsService {
    
    private final Storage storage;
    
    @Value("${gcs.bucket-name}")
    private String bucketName;
    
    /**
     * 업로드용 Presigned URL 생성 (PUT)
     * 
     * @param objectPath GCS 객체 경로
     * @param contentType 파일 Content-Type
     * @param expirationMinutes URL 만료 시간 (분)
     * @return presignedURL
     */
    public String generateUploadUrl(String objectPath, String contentType, int expirationMinutes) {
        try {
            log.info("Generating upload URL for: {}, contentType: {}, expiration: {}min", 
                objectPath, contentType, expirationMinutes);
            
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectPath)
                .setContentType(contentType)
                .build();
            
            URL url = storage.signUrl(
                blobInfo,
                expirationMinutes,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withV4Signature()
            );
            
            log.info("Successfully generated upload URL for: {}", objectPath);
            return url.toString();
            
        } catch (Exception e) {
            log.error("Failed to generate upload URL for: {}", objectPath, e);
            throw new CustomException(ErrorCode.PRESIGNED_URL_FAILED, 
                "업로드 URL 생성 실패: " + objectPath);
        }
    }
    
    /**
     * 다운로드용 Presigned URL 생성 (GET)
     * 
     * @param objectPath GCS 객체 경로
     * @param expirationMinutes URL 만료 시간 (분)
     * @return presignedURL
     */
    public String generateDownloadUrl(String objectPath, int expirationMinutes) {
        try {
            log.debug("Generating download URL for: {}, expiration: {}min", 
                objectPath, expirationMinutes);
            
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectPath).build();
            
            URL url = storage.signUrl(
                blobInfo,
                expirationMinutes,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature()
            );
            
            return url.toString();
            
        } catch (Exception e) {
            log.error("Failed to generate download URL for: {}", objectPath, e);
            throw new CustomException(ErrorCode.PRESIGNED_URL_FAILED, 
                "다운로드 URL 생성 실패: " + objectPath);
        }
    }
    
    /**
     * GCS에서 이미지 삭제
     * 
     * @param objectPath GCS 객체 경로
     */
    public void deleteImage(String objectPath) {
        try {
            log.info("Deleting image from GCS: {}", objectPath);
            
            BlobId blobId = BlobId.of(bucketName, objectPath);
            boolean deleted = storage.delete(blobId);
            
            if (deleted) {
                log.info("Successfully deleted image: {}", objectPath);
            } else {
                log.warn("Image not found in GCS (may already be deleted): {}", objectPath);
            }
            
        } catch (Exception e) {
            log.error("Failed to delete image from GCS: {}", objectPath, e);
            // 삭제 실패해도 예외를 던지지 않음 (이미 삭제되었을 수 있음)
        }
    }
    
    /**
     * GCS에 이미지가 존재하는지 확인
     * 
     * @param objectPath GCS 객체 경로
     * @return 존재 여부
     */
    public boolean exists(String objectPath) {
        try {
            Blob blob = storage.get(bucketName, objectPath);
            return blob != null && blob.exists();
        } catch (Exception e) {
            log.error("Failed to check image existence: {}", objectPath, e);
            return false;
        }
    }
}

