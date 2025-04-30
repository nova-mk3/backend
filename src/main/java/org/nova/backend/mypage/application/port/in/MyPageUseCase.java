package org.nova.backend.mypage.application.port.in;

import java.util.UUID;
import org.nova.backend.mypage.application.dto.response.MyPostsResponse;
import org.nova.backend.mypage.application.dto.response.MySuggestionPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MyPageUseCase {
    Page<MyPostsResponse> getMyPosts(UUID memberId, Pageable pageable);
    Page<MySuggestionPostResponse> getMySuggestionPosts(UUID memberId, Pageable pageable);
}
