package PitterPatter.loventure.content.domain.diary.ui;

import PitterPatter.loventure.content.domain.diary.application.dto.request.CreateDiaryRequest;
import PitterPatter.loventure.content.domain.diary.application.dto.response.CreateDiaryResponse;
import PitterPatter.loventure.content.domain.diary.application.usecase.CreateDiaryUseCase;
import PitterPatter.loventure.content.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries")
public class ContentController {

    private final CreateDiaryUseCase createDiaryUseCase;

    @PostMapping("")
    public BaseResponse<CreateDiaryResponse> createDiary(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Couple-Id") Long coupleId,
            @RequestBody CreateDiaryRequest body
    ) {
        CreateDiaryResponse response = createDiaryUseCase.createDiary(userId, coupleId, body);
        return BaseResponse.success(response);
    }
}
