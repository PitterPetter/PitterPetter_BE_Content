package PitterPatter.loventure.content.domain.diary.domain.repository;

import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    
    // 커플의 전체 다이어리 개수 조회
    long countByCoupleId(Long coupleId);
    
    // 커플의 다이어리 목록을 최신순으로 페이지네이션 조회 (Image도 함께 fetch)
    // TODO: userId로는 조회하지 말기
    @Query("SELECT d FROM Diary d LEFT JOIN FETCH d.image WHERE d.coupleId = :coupleId and d.userId = :userId ORDER BY d.createdAt DESC")
    Page<Diary> findByCoupleIdAndUserIdOrderByCreatedAtDesc(@Param("coupleId") Long coupleId, @Param("userId") Long userId, Pageable pageable);
}
