package org.nova.backend.board.common.adapter.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.application.port.in.BoardUseCase;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board API", description = "게시판 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {
    private final BoardUseCase boardUseCase;

    @Operation(summary = "모든 게시판 조회", description = "전체 게시판 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Board>>> getAllBoards() {
        List<Board> boards = boardUseCase.getAllBoards();
        return ResponseEntity.ok(ApiResponse.success(boards));
    }
}
