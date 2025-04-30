package org.nova.backend.mypage.application.dto.mapper;

import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionPost;
import org.nova.backend.mypage.application.dto.response.MyPostsResponse;
import org.nova.backend.mypage.application.dto.response.MySuggestionPostResponse;

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

    public static MySuggestionPostResponse toMySuggestionPostResponse(SuggestionPost post) {
        return new MySuggestionPostResponse(
                post.getId(),
                post.getMember().getName(),
                post.getTitle(),
                post.getCreatedTime(),
                post.isPrivate(),
                post.isAnswered(),
                post.isAdminRead()
        );
    }

}
