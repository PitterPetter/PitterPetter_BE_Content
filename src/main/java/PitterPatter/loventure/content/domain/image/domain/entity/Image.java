package PitterPatter.loventure.content.domain.image.domain.entity;

import PitterPatter.loventure.content.global.common.BaseTimeEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이미지 엔터티
 * 
 * GCS에 저장되는 이미지의 메타데이터를 관리합니다.
 * presignedURL 방식으로 클라이언트가 직접 GCS에 업로드/다운로드합니다.
 */
@Entity
@Table(name = "image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTimeEntity {
    
    @Id @Tsid
    @Column(name = "image_id")
    private String imageId;
    
    /**
     * 고유 식별자 (UUID)
     * GCS 객체 경로 생성에 사용
     */
    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;
    
    /**
     * GCS 객체 경로
     * 형식: images/diary/{uuid}.{extension}
     */
    @Column(name = "object_path", nullable = false)
    private String objectPath;
    
    /**
     * 이미지 타입 (DIARY, PROFILE 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    private ImageType imageType;
    
    /**
     * 참조 ID (다이어리 ID 등)
     * imageType에 따라 참조하는 엔터티가 다름
     */
    @Column(name = "reference_id")
    private String referenceId;
    
    /**
     * 업로드 상태 (PENDING, UPLOADED, FAILED)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ImageStatus status;
    
    /**
     * 파일 Content-Type (image/jpeg, image/png 등)
     */
    @Column(name = "content_type", nullable = false)
    private String contentType;
    
    /**
     * 파일 크기 (bytes)
     */
    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;
    
    /**
     * 원본 파일명
     */
    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;
    
    @Builder
    public Image(String uuid, String objectPath, ImageType imageType, String referenceId,
                 ImageStatus status, String contentType, Long sizeBytes, String originalFileName) {
        this.uuid = uuid;
        this.objectPath = objectPath;
        this.imageType = imageType;
        this.referenceId = referenceId;
        this.status = status;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.originalFileName = originalFileName;
    }
    
    /**
     * 업로드 완료 처리
     */
    public void markAsUploaded() {
        this.status = ImageStatus.UPLOADED;
    }
    
    /**
     * 업로드 실패 처리
     */
    public void markAsFailed() {
        this.status = ImageStatus.FAILED;
    }
    
    /**
     * referenceId 업데이트
     */
    public void updateReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
