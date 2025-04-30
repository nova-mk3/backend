package org.nova.backend.mypage.adapter.web;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.mypage.adapter.doc.MyPageApiDocument;
import org.nova.backend.mypage.application.dto.response.MyPostsResponse;
import org.nova.backend.mypage.application.port.in.MyPageUseCase;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.nova.backend.board.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MyPage API", description = "마이페이지 전용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {
    private final MyPageUseCase myPageUseCase;
    private final SecurityUtil securityUtil;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts")
    @MyPageApiDocument.GetMyPosts
    public ResponseEntity<ApiResponse<Page<MyPostsResponse>>> getMyPosts(
            @Parameter(hidden = true) Pageable pageable
    ) {
        UUID memberId = securityUtil.getCurrentMemberId();
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdTime"));
        return ResponseEntity.ok(ApiResponse.success(myPageUseCase.getMyPosts(memberId, sorted)));
    }
}
