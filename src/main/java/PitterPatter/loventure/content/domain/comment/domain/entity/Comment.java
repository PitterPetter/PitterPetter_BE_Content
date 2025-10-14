package PitterPatter.loventure.content.domain.comment.domain.entity;

import PitterPatter.loventure.content.global.common.BaseTimeEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment extends BaseTimeEntity {

    @Id @Tsid
    @Column(name = "comment_id")
    private String commentId;

    @Column(name = "diary_id", nullable = false)
    private String diaryId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "author_name", nullable = false, length = 50)
    private String authorName;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public void update(String content) {
        this.content = content;
    }
}

