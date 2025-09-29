package PitterPatter.loventure.content.domain.diary.service;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.domain.entity.Content;
import PitterPatter.loventure.content.domain.diary.domain.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public DiaryResponse saveDiary(Long userId, Long coupleId, CreateDiaryRequest request) {
        // TODO:

        Content content = Content.builder()
                .userId(userId)
                .coupleId(coupleId)
                .courseId(request.courseId())
                .title(request.title())
                .content(request.content())
                .rating(request.rating())
                .build();

        return DiaryResponse.create(contentRepository.save(content));
    }
}
