package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.comment.application.dto.response.CommentResponse;
import PitterPatter.loventure.content.domain.comment.domain.entity.Comment;
import PitterPatter.loventure.content.domain.comment.service.CommentService;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.DiaryServiec;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoadDiaryUseCase {

    private final DiaryServiec diaryServiec;
    private final CommentService commentService;

    public DiaryResponse execute(Long userId, Long coupleId, Long diaryId) {
        Diary diary = diaryServiec.findByDiaryId(diaryId);

        //다이어 작성 커플과 요청 커플과 다르면 익셉션
        if(!diary.getCoupleId().equals(coupleId)) {
            throw new CustomException(ErrorCode.DIARY402);
        }

        // 댓글 목록 조회
        List<Comment> comments = commentService.findByDiaryId(diaryId);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());

        return DiaryResponse.createWithComments(diary, commentResponses);
    }
}
