package PitterPatter.loventure.content.domain.diary.application.usecase;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.application.service.UserLookupService;
import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.service.DiaryServiec;
import PitterPatter.loventure.content.domain.image.application.service.ImageService;
import PitterPatter.loventure.content.domain.image.domain.entity.Image;
import PitterPatter.loventure.content.domain.image.domain.entity.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CreateDiaryUseCase {

    private final DiaryServiec diaryServiec;
    private final UserLookupService userLookupService;
    private final ImageService imageService;

    @Transactional
    public DiaryResponse execute(String token, Long userId, Long coupleId, CreateDiaryRequest request, MultipartFile imageFile) {

        String author = userLookupService.getUserName(userId, token);

        // Diary entity 생성
        Diary diary = diaryServiec.saveDiary(userId, author, coupleId, request);

        // 이미지 파일이 있으면 업로드
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = imageService.uploadImage(imageFile, ImageType.DIARY, diary.getDiaryId());
            diary.updateImageId(image.getImageId());
            imageUrl = imageService.getSignedUrl(image.getImageId());
        }

        return DiaryResponse.create(diary, imageUrl);
    }
}
