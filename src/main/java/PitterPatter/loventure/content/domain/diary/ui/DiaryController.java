package PitterPatter.loventure.content.domain.diary.ui;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.request.UpdateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryListResponse;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.application.usecase.*;
import PitterPatter.loventure.content.global.common.BaseResponse;
import PitterPatter.loventure.content.global.annotation.CurrentUser;
import PitterPatter.loventure.content.global.annotation.CurrentCouple;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries")
@Tag(name = "Diary", description = "다이어리 관리 API")
public class DiaryController {

    private final CreateDiaryUseCase createDiaryUseCase;
    private final LoadDiaryListUseCase loadDiaryListUseCase;
    private final LoadDiaryUseCase loadDiaryUseCase;
    private final UpdateDiaryUseCase updateDiaryUseCase;
    private final DeleteDiaryUseCase deleteDiaryUseCase;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다이어리 생성", description = "다이어리 정보는 JSON, 이미지는 파일로 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "다이어리 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<DiaryResponse> createDiary(
            @Parameter(description = "토큰값", hidden = true) @CurrentUser String token,
            @Parameter(description = "JWT 토큰에서 추출된 사용자 ID", hidden = true) @CurrentUser Long userId,
            @Parameter(description = "JWT 토큰에서 추출된 커플 ID", hidden = true) @CurrentCouple Long coupleId,
            @Parameter(description = "다이어리 생성 요청 정보 (JSON)", required = true) @Valid @RequestPart("diary") CreateDiaryRequest body,
            @Parameter(description = "이미지 파일 (선택사항)") @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        DiaryResponse response = createDiaryUseCase.execute(token, userId, coupleId, body, image);
        return BaseResponse.success(response);
    }

    @GetMapping("")
    @Operation(summary = "다이어리 리스트 조회")
    public BaseResponse<DiaryListResponse> loadDiaryList(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Parameter(hidden = true) @CurrentCouple Long coupleId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return BaseResponse.success(loadDiaryListUseCase.execute(userId, coupleId, page, size));
    }

    @GetMapping("/{diaryId}")
    @Operation(summary = "다이어리 상세 조회")
    public BaseResponse<DiaryResponse> loadDiary(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Parameter(hidden = true) @CurrentCouple Long coupleId,
            @PathVariable Long diaryId
    ) {
        return BaseResponse.success(loadDiaryUseCase.execute(userId, coupleId, diaryId));
    }

    @PostMapping(value = "/{diaryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다이어리 수정", description = "다이어리 정보는 JSON, 이미지는 파일로 전송합니다.")
    public BaseResponse<DiaryResponse> updateDiary(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Parameter(hidden = true) @CurrentCouple Long coupleId,
            @PathVariable Long diaryId,
            @Parameter(description = "다이어리 수정 요청 정보 (JSON)") @Valid @RequestPart("diary") UpdateDiaryRequest request,
            @Parameter(description = "이미지 파일 (선택사항)") @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return BaseResponse.success(updateDiaryUseCase.execute(diaryId, userId, coupleId, request, image));
    }

    @DeleteMapping("/{diaryId}")
    @Operation(summary = "다이어리 삭제", description = "다이어리와 연결된 이미지도 함께 삭제됩니다.")
    public BaseResponse<Void>  deleteDiary(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Parameter(hidden = true) @CurrentCouple Long coupleId,
            @PathVariable Long diaryId
    ) {
        return BaseResponse.success(deleteDiaryUseCase.execute(userId, coupleId, diaryId));
    }
}
