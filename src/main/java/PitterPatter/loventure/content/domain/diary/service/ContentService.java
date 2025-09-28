package PitterPatter.loventure.content.domain.diary.service;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryListResponse;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiarySummary;
import PitterPatter.loventure.content.domain.diary.application.dto.response.PageInfo;
import PitterPatter.loventure.content.domain.diary.domain.entity.Content;
import PitterPatter.loventure.content.domain.diary.domain.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                .courseId(request.course_id())
                .title(request.title())
                .content(request.content())
                .rating(request.rating())
                .build();

        return DiaryResponse.create(contentRepository.save(content));
    }

    public int getAllDiaryCount(Long coupleId) {
        return (int) contentRepository.countByCoupleId(coupleId);
    }

    public DiaryListResponse loadDiaryList(Long userId, Long coupleId, int page, int size) {
        // 페이지 번호를 0부터 시작하도록 조정 (Spring Data JPA는 0부터 시작)
        Pageable pageable = PageRequest.of(page, size);
        
        // 커플의 다이어리 목록을 최신순으로 페이지네이션 조회
        Page<Content> contentPage = contentRepository.findByCoupleIdOrderByCreatedAtDesc(coupleId, pageable);
        
        // Content 엔티티를 DiarySummary로 변환
        var diarySummaries = contentPage.getContent().stream()
                .map(this::convertToDiarySummary)
                .toList();
        
        // 페이지 정보 생성
        PageInfo pageInfo = PageInfo.builder()
                .page(page)
                .size(size)
                .totalElements(contentPage.getTotalElements())
                .totalPages(contentPage.getTotalPages())
                .build();
        
        return DiaryListResponse.builder()
                .content(diarySummaries)
                .page(pageInfo)
                .build();
    }
    
    private DiarySummary convertToDiarySummary(Content content) {
        // content에서 excerpt 생성 (첫 100자만)
        String excerpt = content.getContent().length() > 32
                ? content.getContent().substring(0, 32) + "..."
                : content.getContent();

        return DiarySummary.builder()
                .diaryId(content.getContentId())
                .title(content.getTitle())
                .excerpt(excerpt)
                .updatedAt(content.getUpdatedAt())
                .likeCount(0) // TODO: 좋아요 기능 구현 시 실제 값으로 변경
                .build();
    }
}
