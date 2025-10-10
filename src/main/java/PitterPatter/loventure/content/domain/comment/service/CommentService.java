package PitterPatter.loventure.content.domain.comment.service;

import PitterPatter.loventure.content.domain.comment.application.dto.request.CreateCommentRequest;
import PitterPatter.loventure.content.domain.comment.domain.entity.Comment;
import PitterPatter.loventure.content.domain.comment.domain.repository.CommentRepository;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public Comment saveComment(Long diaryId, Long userId, String authorName, CreateCommentRequest request) {
        Comment comment = Comment.builder()
                .diaryId(diaryId)
                .userId(userId)
                .authorName(authorName)
                .content(request.content())
                .build();
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT404));
    }

    @Transactional(readOnly = true)
    public List<Comment> findByDiaryId(Long diaryId) {
        return commentRepository.findByDiaryIdOrderByCreatedAtAsc(diaryId);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void deleteCommentsByDiaryId(Long diaryId) {
        commentRepository.deleteByDiaryId(diaryId);
    }
}

