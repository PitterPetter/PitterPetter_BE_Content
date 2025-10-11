package PitterPatter.loventure.content.domain.diary.service;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryListResponse;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiarySummary;
import PitterPatter.loventure.content.domain.diary.application.dto.response.PageInfo;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.domain.repository.DiaryRepository;
import PitterPatter.loventure.content.domain.image.service.ImageService;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageService imageService;

    public Diary saveDiary(Long userId, String author, Long coupleId, CreateDiaryRequest request) {
        // 다이어리 엔터티 빌더로 생성
        Diary diary = Diary.builder()
                .userId(userId)
                .coupleId(coupleId)
                .courseId(request.courseId())
                .title(request.title())
                .content(request.content())
                .rating(request.rating())
                .authorName(author)
                .build();

        // 저장 및 Diary 형태로 반환
        return diaryRepository.save(diary);
    }

    public int getAllDiaryCount(Long coupleId) {
        return (int) diaryRepository.countByCoupleId(coupleId);
    }

    public DiaryListResponse loadDiaryList(Long userId, Long coupleId, int page, int size) {
        // 페이지 번호를 0부터 시작하도록 조정 (Spring Data JPA는 0부터 시작)
        Pageable pageable = PageRequest.of(page, size);
        
        // 커플의 다이어리 목록을 최신순으로 페이지네이션 조회
        Page<Diary> contentPage = diaryRepository.findByCoupleIdAndUserIdOrderByCreatedAtDesc(coupleId, userId, pageable);

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
    
    private DiarySummary convertToDiarySummary(Diary diary) {
        // content에서 excerpt 생성 (첫 32자만)
        String excerpt = diary.getContent().length() > 32
                ? diary.getContent().substring(0, 32) + "..."
                : diary.getContent();

        // 이미지 다운로드 URL 생성 (UPLOADED 상태인 경우만)
        String imageUrl = null;
        Long imageId = null;
        String imageStatus = null;
        
        if (diary.getImage() != null) {
            imageId = diary.getImage().getId();
            imageStatus = diary.getImage().getStatus().name();
            imageUrl = imageService.generateDownloadUrl(imageId);
        }

        return DiarySummary.builder()
                .diaryId(diary.getDiaryId())
                .title(diary.getTitle())
                .excerpt(excerpt)
                .updatedAt(diary.getUpdatedAt())
                .likeCount(0) // TODO: 좋아요 기능 구현 시 실제 값으로 변경
                .imageId(imageId)
                .imageUrl(imageUrl)
                .imageStatus(imageStatus)
                .imageExpiresIn(imageUrl != null ? 3600 : null) // 1시간
                .build();
    }

    // 다이어리 엔터티 받아서 제목, 내용 수정
    public DiaryResponse updateDiary(Diary diary, String title, String content) {
        diary.update(title, content);
        return DiaryResponse.create(diary);
    }

    // 다이어리 엔터티 반환
    public Diary findByDiaryId(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY404));
    }

    // 다이어리 하드 삭제
    public void deleteDiary(Diary diary) {
        diaryRepository.delete(diary);
    }
}
