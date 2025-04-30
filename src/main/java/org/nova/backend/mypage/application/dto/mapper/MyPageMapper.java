package org.nova.backend.mypage.application.dto.mapper;

import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.mypage.application.dto.response.MyPostsResponse;

public class MyPageMapper {
    private MyPageMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static MyPostsResponse toMyPostsResponse(Post post) {
        return new MyPostsResponse(
                post.getId(),
                post.getPostType(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedTime(),
                post.getMember().getName()
        );
    }
}
