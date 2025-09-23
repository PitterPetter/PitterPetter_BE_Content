package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.Author;
import PitterPatter.loventure.content.domain.diary.application.dto.response.CreateDiaryResponse;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateDiaryUseCase {

    private final ContentService contentService;

    public CreateDiaryResponse createDiary(Long userId, Long coupleId, CreateDiaryRequest request) {
        // Diary 엔티티 생성
        Diary diary = Diary.builder()
                .userId(userId)
                .coupleId(coupleId)
                .courseId(request.course_id())
                .title(request.title())
                .content(request.content())
                .build();

        // 서비스를 통해 저장
        Diary savedDiary = contentService.saveDiary(diary);

        // Author 정보 생성 (현재는 간단하게 userId만 사용)
        Author author = Author.builder()
                .userId(userId)
                .nickname("사용자" + userId) // 임시 닉네임, 실제로는 User 서비스에서 가져와야 함
                .build();

        // Response 생성
        return CreateDiaryResponse.builder()
                .diaryId(savedDiary.getDiaryId())
                .title(savedDiary.getTitle())
                .content(savedDiary.getContent())
                .courseId(savedDiary.getCourseId())
                .createdAt(savedDiary.getCreatedAt())
                .updatedAt(savedDiary.getUpdatedAt())
                .likeCount(0) // 초기값
                .isLiked(false) // 초기값
                .author(author)
                .build();
    }
}
