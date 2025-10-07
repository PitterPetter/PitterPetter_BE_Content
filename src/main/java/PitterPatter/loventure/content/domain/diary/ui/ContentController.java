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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries")
@Tag(name = "Diary", description = "다이어리 관리 API")
public class ContentController {

    private final CreateDiaryUseCase createDiaryUseCase;
    private final LoadDiaryListUseCase loadDiaryListUseCase;
    private final LoadDiaryUseCase loadDiaryUseCase;
    private final UpdateDiaryUseCase updateDiaryUseCase;
    private final DeleteDiaryUseCase deleteDiaryUseCase;

    @PostMapping("")
    @Operation(summary = "다이어리 생성", description = "새로운 다이어리를 생성합니다. JWT 토큰에서 사용자 ID와 커플 ID를 자동으로 추출합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "다이어리 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<DiaryResponse> createDiary(
            @Parameter(description = "JWT 토큰에서 추출된 사용자 ID", hidden = true) @CurrentUser Long userId,
            @Parameter(description = "JWT 토큰에서 추출된 커플 ID", hidden = true) @CurrentCouple Long coupleId,
            @Parameter(description = "다이어리 생성 요청 정보", required = true) @Valid @RequestBody CreateDiaryRequest body
    ) {
        DiaryResponse response = createDiaryUseCase.execute(userId, coupleId, body);
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

    @PostMapping("/{diaryId}")
    @Operation(summary = "다이어리 수정")
    public BaseResponse<DiaryResponse> updateDiary(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Parameter(hidden = true) @CurrentCouple Long coupleId,
            @PathVariable Long diaryId,
            @RequestBody UpdateDiaryRequest request
    ) {
        return BaseResponse.success(updateDiaryUseCase.execute(diaryId, userId, coupleId, request));
    }

    @DeleteMapping("/{diaryId}")
    @Operation(summary = "다이어리 삭제")
    public BaseResponse<Void>  deleteDiary(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Parameter(hidden = true) @CurrentCouple Long coupleId,
            @PathVariable Long diaryId
    ) {
        return BaseResponse.success(deleteDiaryUseCase.execute(userId, coupleId, diaryId));
    }
}
