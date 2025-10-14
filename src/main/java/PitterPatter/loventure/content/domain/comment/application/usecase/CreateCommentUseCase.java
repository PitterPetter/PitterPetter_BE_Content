package PitterPatter.loventure.content.domain.comment.application.usecase;

import PitterPatter.loventure.content.domain.comment.application.dto.request.CreateCommentRequest;
import PitterPatter.loventure.content.domain.comment.application.dto.response.CommentIdResponse;
import PitterPatter.loventure.content.domain.comment.domain.entity.Comment;
import PitterPatter.loventure.content.domain.comment.service.CommentService;
import PitterPatter.loventure.content.domain.diary.application.service.UserLookupService;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.DiaryService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCommentUseCase {

    private final CommentService commentService;
    private final DiaryService diaryService;
    private final UserLookupService userLookupService;

    @Transactional
    public CommentIdResponse execute(String userId, String coupleId, String diaryId, CreateCommentRequest request) {
        // 다이어리 존재 여부 확인 및 커플 권한 확인
        Diary diary = diaryService.findByDiaryId(diaryId);
        if (!diary.getCoupleId().equals(coupleId)) {
            throw new CustomException(ErrorCode.DIARY402);
        }

        // 사용자 이름 조회
        String authorName = userLookupService.getUserName(userId);

        // 댓글 생성
        Comment comment = commentService.saveComment(diaryId, userId, authorName, request);

        return CommentIdResponse.of(comment.getCommentId());
    }
}

