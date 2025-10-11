package PitterPatter.loventure.content.domain.image.domain.repository;

import PitterPatter.loventure.content.domain.image.domain.ImageStatus;
import PitterPatter.loventure.content.domain.image.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    
    /**
     * PENDING 상태이면서 생성일이 특정 시점 이전인 이미지 조회
     * (향후 스케줄러에서 사용)
     */
    List<Image> findByStatusAndCreatedAtBefore(ImageStatus status, LocalDateTime threshold);
}

