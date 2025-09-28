package PitterPatter.loventure.content.domain.diary.domain.repository;

import PitterPatter.loventure.content.domain.diary.domain.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentRepository extends JpaRepository<Content, Long> {
    
    // 커플의 전체 다이어리 개수 조회
    long countByCoupleId(Long coupleId);
    
    // 커플의 다이어리 목록을 최신순으로 페이지네이션 조회
    @Query("SELECT c FROM Content c WHERE c.coupleId = :coupleId ORDER BY c.createdAt DESC")
    Page<Content> findByCoupleIdOrderByCreatedAtDesc(@Param("coupleId") Long coupleId, Pageable pageable);
}
