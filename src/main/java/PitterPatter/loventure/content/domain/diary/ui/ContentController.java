package PitterPatter.loventure.content.domain.diary.ui;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.DiaryResponse;
import PitterPatter.loventure.content.domain.diary.application.usecase.CreateDiaryUseCase;
import PitterPatter.loventure.content.global.common.BaseResponse;
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

    @PostMapping("")
    @Operation(summary = "다이어리 생성", description = "새로운 다이어리를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "다이어리 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<DiaryResponse> createDiary(
            // TODO: 머지 후 @CurrentUser, @CurrentCouple 로 바꾸기
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @Parameter(hidden = true) @RequestHeader("X-Couple-Id") Long coupleId,
            @Parameter(description = "다이어리 생성 요청 정보", required = true) @Valid @RequestBody CreateDiaryRequest body
    ) {
        DiaryResponse response = createDiaryUseCase.execute(userId, coupleId, body);
        return BaseResponse.success(response);
    }
}
