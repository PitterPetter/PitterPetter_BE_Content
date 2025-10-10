package PitterPatter.loventure.content.domain.image.domain.entity;

import PitterPatter.loventure.content.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "image")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    // GCS에 저장된 파일명 (예: "images/diary-123-uuid.jpg")
    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    // 원본 파일명
    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    // 파일 크기 (bytes)
    @Column(name = "file_size")
    private Long fileSize;

    // 컨텐츠 타입 (image/jpeg, image/png 등)
    @Column(name = "content_type", length = 100)
    private String contentType;

    // 이미지 타입 (DIARY, PROFILE, COMMENT 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false, length = 50)
    private ImageType imageType;

    // 참조 ID (다이어리 ID, 유저 ID 등)
    @Column(name = "reference_id")
    private Long referenceId;

    // 소프트 삭제 여부
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    /**
     * 이미지 소프트 삭제
     */
    public void delete() {
        this.isDeleted = true;
    }

    /**
     * 참조 ID 업데이트 (다이어리 생성 후 연결 시 사용)
     */
    public void updateReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
}

