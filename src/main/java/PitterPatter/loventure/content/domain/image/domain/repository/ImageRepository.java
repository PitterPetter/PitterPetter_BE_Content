package PitterPatter.loventure.content.domain.image.domain.repository;

import PitterPatter.loventure.content.domain.image.domain.entity.Image;
import PitterPatter.loventure.content.domain.image.domain.entity.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    
    /**
     * 참조 ID와 타입으로 이미지 조회 (삭제되지 않은 것만)
     */
    Optional<Image> findByReferenceIdAndImageTypeAndIsDeletedFalse(Long referenceId, ImageType imageType);
    
    /**
     * 이미지 ID로 조회 (삭제되지 않은 것만)
     */
    Optional<Image> findByImageIdAndIsDeletedFalse(Long imageId);
}

