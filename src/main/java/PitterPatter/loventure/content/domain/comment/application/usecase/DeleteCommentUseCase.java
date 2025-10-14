package PitterPatter.loventure.content.domain.comment.application.usecase;

import PitterPatter.loventure.content.domain.comment.domain.entity.Comment;
import PitterPatter.loventure.content.domain.comment.service.CommentService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCommentUseCase {

    private final CommentService commentService;

    @Transactional
    public void execute(String userId, String commentId) {
        // 댓글 존재 확인
        Comment comment = commentService.findById(commentId);

        // 작성자 본인 확인
        if (!comment.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMENT401);
        }

        // 댓글 삭제
        commentService.deleteComment(commentId);
    }
}

