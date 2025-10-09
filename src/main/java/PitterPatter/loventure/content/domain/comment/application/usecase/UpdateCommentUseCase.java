package PitterPatter.loventure.content.domain.comment.application.usecase;

import PitterPatter.loventure.content.domain.comment.application.dto.request.UpdateCommentRequest;
import PitterPatter.loventure.content.domain.comment.application.dto.response.CommentIdResponse;
import PitterPatter.loventure.content.domain.comment.domain.entity.Comment;
import PitterPatter.loventure.content.domain.comment.service.CommentService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCommentUseCase {

    private final CommentService commentService;

    @Transactional
    public CommentIdResponse execute(Long userId, Long commentId, UpdateCommentRequest request) {
        // 댓글 존재 확인
        Comment comment = commentService.findById(commentId);

        // 작성자 본인 확인
        if (!comment.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMENT401);
        }



        // 댓글 수정
        comment.update(request.content());

        return CommentIdResponse.of(comment.getCommentId());
    }
}

