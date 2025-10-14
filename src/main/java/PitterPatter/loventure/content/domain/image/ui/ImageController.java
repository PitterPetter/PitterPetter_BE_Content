package PitterPatter.loventure.content.domain.image.ui;

import PitterPatter.loventure.content.domain.image.application.usecase.CompleteImageUploadUseCase;
import PitterPatter.loventure.content.domain.image.application.usecase.DeleteImageUseCase;
import PitterPatter.loventure.content.domain.image.application.usecase.FailImageUploadUseCase;
import PitterPatter.loventure.content.global.annotation.CurrentCouple;
import PitterPatter.loventure.content.global.annotation.CurrentUser;
import PitterPatter.loventure.content.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 이미지 관리 API
 * 
 * 이미지 업로드 완료/실패 처리 및 삭제를 담당합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Tag(name = "Image", description = "이미지 관리 API")
public class ImageController {
    
    private final CompleteImageUploadUseCase completeImageUploadUseCase;
    private final FailImageUploadUseCase failImageUploadUseCase;
    private final DeleteImageUseCase deleteImageUseCase;
    
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
            @Parameter(description = "이미지 ID") @PathVariable String imageId
    ) {
        completeImageUploadUseCase.execute(imageId);
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
            @Parameter(description = "이미지 ID") @PathVariable String imageId
    ) {
        failImageUploadUseCase.execute(imageId);
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
            @Parameter(hidden = true) @CurrentUser String userId,
            @Parameter(hidden = true) @CurrentCouple String coupleId,
            @Parameter(description = "이미지 ID") @PathVariable String imageId
    ) {
        deleteImageUseCase.execute(imageId, userId, coupleId);
        return BaseResponse.success(null);
    }
}
