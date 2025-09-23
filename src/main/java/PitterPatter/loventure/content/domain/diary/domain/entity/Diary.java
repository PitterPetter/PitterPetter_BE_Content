package PitterPatter.loventure.content.domain.diary.domain.entity;

import PitterPatter.loventure.content.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Diary extends BaseTimeEntity {

    // pk: diary_id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long diaryId;

    // msa 환경: 다른 서비스의 fk는 물리적 제약 없이 보관
    // 필수: couple_id
    @Column(name = "couple_id", nullable = false)
    private Long coupleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 선택: course_id (없을 수 있음)
    @Column(name = "course_id")
    private Long courseId;

    // 평점: 소수점(예: 4.8). 금액/정밀도가 중요한 도메인이 아니므로 Double로 저장
    // 필요 시 precision/scale을 조절하려면 BigDecimal 사용 고려
    @Column(name = "rating")
    private Double rating;

    // 필수: title, content
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
