package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDiaryUseCase {

    private final ContentService contentService;

    @Transactional
    public DiaryResponse execute(Long userId, Long coupleId, CreateDiaryRequest request) {

        // auth 서비스에 유저 정보 요청하는 건 추후에
        /*Author author = Author.builder()
                .userId(userId)
                .nickname("사용자" + userId) // 임시 닉네임, 실제로는 User 서비스에서 가져와야 함
                .build();*/

        return contentService.saveDiary(userId, coupleId, request);
    }
}
