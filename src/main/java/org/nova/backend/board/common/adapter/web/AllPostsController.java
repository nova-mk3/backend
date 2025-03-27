package org.nova.backend.board.common.adapter.web;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.adapter.doc.AllPostsApiDocument;
import org.nova.backend.board.common.application.dto.response.AllPostSummaryResponse;
import org.nova.backend.board.common.application.port.in.BasePostUseCase;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "All Post API", description = "EXAM_ARCHIVE를 제외한 전체 게시글 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class AllPostsController {
    private final BasePostUseCase basePostUseCase;

    @GetMapping("/across-boards")
    @AllPostsApiDocument.GetAllPostsAcrossBoards
    public ResponseEntity<ApiResponse<Page<AllPostSummaryResponse>>> getAllPostsAcrossBoards(
            @RequestParam(required = false, defaultValue = "createdTime") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<AllPostSummaryResponse> posts = basePostUseCase.getAllPostsFromAllBoards(sortedPageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
}
