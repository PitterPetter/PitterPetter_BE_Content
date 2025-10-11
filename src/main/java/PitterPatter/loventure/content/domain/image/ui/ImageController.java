package PitterPatter.loventure.content.domain.image.ui;

import PitterPatter.loventure.content.domain.diary.domain.entity.Diary;
import PitterPatter.loventure.content.domain.diary.domain.repository.DiaryRepository;
import PitterPatter.loventure.content.domain.image.domain.entity.Image;
import PitterPatter.loventure.content.domain.image.service.ImageService;
import PitterPatter.loventure.content.global.annotation.CurrentCouple;
import PitterPatter.loventure.content.global.annotation.CurrentUser;
import PitterPatter.loventure.content.global.common.BaseResponse;
import PitterPatter.loventure.content.global.error.CustomException;
import PitterPatter.loventure.content.global.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 이미지 관리 API
 * 
 * 이미지 업로드 완료/실패 처리 및 삭제를 담당합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Tag(name = "Image", description = "이미지 관리 API")
public class ImageController {
    
    private final ImageService imageService;
    private final DiaryRepository diaryRepository;
    
    /**
     * 이미지 업로드 완료 콜백
     * 
     * 프론트엔드가 GCS에 이미지를 성공적으로 업로드한 후 호출합니다.
     */
    @PatchMapping("/{imageId}/complete")
    @Operation(summary = "이미지 업로드 완료", description = "GCS에 이미지 업로드 완료 후 호출하여 상태를 UPLOADED로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 완료 처리 성공"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    public BaseResponse<Void> completeImageUpload(
            @Parameter(description = "이미지 ID") @PathVariable Long imageId
    ) {
        log.info("Image upload complete callback: imageId={}", imageId);
        imageService.markAsUploaded(imageId);
        return BaseResponse.success(null);
    }
    
    /**
     * 이미지 업로드 실패 콜백
     * 
     * 프론트엔드가 GCS에 이미지 업로드 실패 시 호출합니다.
     */
    @PatchMapping("/{imageId}/fail")
    @Operation(summary = "이미지 업로드 실패", description = "GCS에 이미지 업로드 실패 시 호출하여 상태를 FAILED로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 실패 처리 성공"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    public BaseResponse<Void> failImageUpload(
            @Parameter(description = "이미지 ID") @PathVariable Long imageId
    ) {
        log.info("Image upload fail callback: imageId={}", imageId);
        imageService.markAsFailed(imageId);
        return BaseResponse.success(null);
    }
    
    /**
     * 이미지 삭제
     * 
     * 다이어리에서 이미지만 삭제하고 싶을 때 사용합니다.
     * 다이어리 작성자만 삭제할 수 있습니다.
     */
    @DeleteMapping("/{imageId}")
    @Operation(summary = "이미지 삭제", description = "다이어리의 이미지만 삭제합니다. 다이어리 작성자만 삭제할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "이미지 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    public BaseResponse<Void> deleteImage(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Parameter(hidden = true) @CurrentCouple Long coupleId,
            @Parameter(description = "이미지 ID") @PathVariable Long imageId
    ) {
        log.info("Delete image request: imageId={}, userId={}, coupleId={}", imageId, userId, coupleId);
        
        // 1. 이미지 조회
        Image image = imageService.getImage(imageId);
        
        // 2. 연관된 다이어리 조회 및 권한 체크
        Diary diary = diaryRepository.findById(image.getReferenceId())
            .orElseThrow(() -> new CustomException(ErrorCode.DIARY404));
        
        // 3. 작성자 확인
        if (!diary.getUserId().equals(userId)) {
            log.warn("Image delete access denied: imageId={}, userId={}, diaryUserId={}", 
                imageId, userId, diary.getUserId());
            throw new CustomException(ErrorCode.IMAGE_ACCESS_DENIED);
        }
        
        // 4. coupleId 확인 TODO: 커플 권한 체크가 필요한지 확인하기
        if (!diary.getCoupleId().equals(coupleId)) {
            log.warn("Image delete access denied (couple mismatch): imageId={}, coupleId={}, diaryCoupleId={}", 
                imageId, coupleId, diary.getCoupleId());
            throw new CustomException(ErrorCode.DIARY402);
        }
        
        // 5. 다이어리에서 이미지 참조 제거 (FK 해제)
        diary.removeImage();
        diaryRepository.save(diary);
        
        // 6. 이미지 삭제 (GCS + DB)
        imageService.deleteImage(imageId);
        
        log.info("Image deleted successfully: imageId={}", imageId);
        return BaseResponse.success(null);
    }
}
