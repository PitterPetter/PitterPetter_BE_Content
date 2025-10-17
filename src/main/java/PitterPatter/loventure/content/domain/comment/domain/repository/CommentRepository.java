package PitterPatter.loventure.content.domain.comment.domain.repository;

import PitterPatter.loventure.content.domain.comment.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByDiaryIdOrderByUpdatedAtAsc(String diaryId);
    void deleteByDiaryId(String diaryId);
    long countByDiaryId(String diaryId);
}

