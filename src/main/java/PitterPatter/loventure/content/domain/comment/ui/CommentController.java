package PitterPatter.loventure.content.domain.comment.ui;

import PitterPatter.loventure.content.domain.comment.application.dto.request.CreateCommentRequest;
import PitterPatter.loventure.content.domain.comment.application.dto.request.UpdateCommentRequest;
import PitterPatter.loventure.content.domain.comment.application.dto.response.CommentIdResponse;
import PitterPatter.loventure.content.domain.comment.application.usecase.CreateCommentUseCase;
import PitterPatter.loventure.content.domain.comment.application.usecase.DeleteCommentUseCase;
import PitterPatter.loventure.content.domain.comment.application.usecase.UpdateCommentUseCase;
import PitterPatter.loventure.content.global.annotation.CurrentCouple;
import PitterPatter.loventure.content.global.annotation.CurrentUser;
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
@RequestMapping("/api/diaries/{diaryId}/comments")
@Tag(name = "Comment", description = "댓글 관리 API")
public class CommentController {

    private final CreateCommentUseCase createCommentUseCase;
    private final UpdateCommentUseCase updateCommentUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;

    @PostMapping("")
    @Operation(summary = "댓글 생성", description = "다이어리에 댓글을 작성합니다. JWT 토큰에서 사용자 ID와 커플 ID를 자동으로 추출합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (다른 커플의 다이어리)"),
            @ApiResponse(responseCode = "404", description = "다이어리를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<CommentIdResponse> createComment(
            @Parameter(description = "JWT 토큰에서 추출된 사용자 ID", hidden = true) @CurrentUser String userId,
            @Parameter(description = "JWT 토큰에서 추출된 커플 ID", hidden = true) @CurrentCouple String coupleId,
            @Parameter(description = "다이어리 ID", required = true) @PathVariable String diaryId,
            @Parameter(description = "댓글 생성 요청 정보", required = true) @Valid @RequestBody CreateCommentRequest request
    ) {
        CommentIdResponse response = createCommentUseCase.execute(userId, coupleId, diaryId, request);
        return BaseResponse.success(response);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "작성한 댓글을 수정합니다. 작성자 본인만 수정할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음 (작성자 아님)"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<CommentIdResponse> updateComment(
            @Parameter(description = "JWT 토큰에서 추출된 사용자 ID", hidden = true) @CurrentUser String userId,
            @Parameter(description = "JWT 토큰에서 추출된 커플 ID", hidden = true) @CurrentCouple String coupleId,
            @Parameter(description = "다이어리 ID", required = true) @PathVariable String diaryId,
            @Parameter(description = "댓글 ID", required = true) @PathVariable String commentId,
            @Parameter(description = "댓글 수정 요청 정보", required = true) @Valid @RequestBody UpdateCommentRequest request
    ) {
        CommentIdResponse response = updateCommentUseCase.execute(userId, commentId, request);
        return BaseResponse.success(response);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제합니다. 작성자 본인만 삭제할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음 (작성자 아님)"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<Void> deleteComment(
            @Parameter(description = "JWT 토큰에서 추출된 사용자 ID", hidden = true) @CurrentUser String userId,
            @Parameter(description = "JWT 토큰에서 추출된 커플 ID", hidden = true) @CurrentCouple String coupleId,
            @Parameter(description = "다이어리 ID", required = true) @PathVariable String diaryId,
            @Parameter(description = "댓글 ID", required = true) @PathVariable String commentId
    ) {
        deleteCommentUseCase.execute(userId, commentId);
        return BaseResponse.success(null);
    }
}

