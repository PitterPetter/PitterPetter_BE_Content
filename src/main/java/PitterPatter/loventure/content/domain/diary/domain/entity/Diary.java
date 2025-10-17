package PitterPatter.loventure.content.domain.diary.domain.entity;

import PitterPatter.loventure.content.domain.image.domain.entity.Image;
import PitterPatter.loventure.content.global.common.BaseTimeEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
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
    @Id @Tsid
    @Column(name = "diary_id")
    private String diaryId;

    // 필수: couple_id
    @Column(name = "couple_id", nullable = false)
    private String coupleId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    // 작성자 이름 (auth 서비스에서 받아온 정보)
    @Column(name = "author_name", nullable = false, length = 50)
    private String authorName;

    // 필수: course_id
    @Column(name = "course_id", nullable = false)
    private String courseId;

    // 평점: 소수점(예: 4.8). 금액/정밀도가 중요한 도메인이 아니므로 Double로 저장
    @Column(name = "rating")
    private Double rating;

    // 필수: title, content
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 이미지 (선택)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    public void update(String title, String content, Double rating) {
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    /**
     * 이미지 연결
     */
    public void updateImage(Image image) {
        this.image = image;
    }

    /**
     * 이미지 제거
     */
    public void removeImage() {
        this.image = null;
    }
}
