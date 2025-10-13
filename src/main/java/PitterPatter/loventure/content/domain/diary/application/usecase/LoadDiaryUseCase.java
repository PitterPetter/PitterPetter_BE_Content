package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.comment.application.dto.response.CommentResponse;
import PitterPatter.loventure.content.domain.comment.domain.entity.Comment;
import PitterPatter.loventure.content.domain.comment.service.CommentService;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.DiaryService;
import PitterPatter.loventure.content.domain.image.service.ImageService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadDiaryUseCase {

    private final DiaryService diaryService;
    private final CommentService commentService;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public DiaryResponse execute(Long userId, Long coupleId, Long diaryId) {
        log.info("Loading diary: diaryId={}, userId={}, coupleId={}", diaryId, userId, coupleId);
        
        Diary diary = diaryService.findByDiaryId(diaryId);

        // 다이어리 작성 커플과 요청 커플이 다르면 예외
        if(!diary.getCoupleId().equals(coupleId)) {
            throw new CustomException(ErrorCode.DIARY402);
        }

        // 댓글 목록 조회
        List<Comment> comments = commentService.findByDiaryId(diaryId);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());

        // 이미지 다운로드 URL 생성 (UPLOADED 상태인 경우만)
        String imageUrl = null;
        if (diary.getImage() != null) {
            imageUrl = imageService.generateDownloadUrl(diary.getImage().getImageId());
            log.debug("Generated download URL for diary: diaryId={}, imageId={}, hasUrl={}", 
                diaryId, diary.getImage().getImageId(), imageUrl != null);
        }

        return DiaryResponse.createWithImageUrl(diary, imageUrl, commentResponses);
    }
}
