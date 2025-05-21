package org.nova.backend.board.common.domain.model.valueobject;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum PostType {
    QNA("Q&A", BoardCategory.INTEGRATED),
    FREE("자유게시판", BoardCategory.INTEGRATED),
    INTRODUCTION("자기소개", BoardCategory.INTEGRATED),
    NOTICE("공지사항", BoardCategory.INTEGRATED),
    EXAM_ARCHIVE("족보 게시판", BoardCategory.CLUB_ARCHIVE),
    PICTURES("사진 게시판", BoardCategory.CLUB_ARCHIVE),
    SUGGESTION("건의 게시판", null);

    private final String displayName;
    private final BoardCategory category;

    PostType(String displayName, BoardCategory category) {
        this.displayName = displayName;
        this.category = category;
    }

    /** 특정 게시판의 PostType 인지 검증 */
    public static boolean isValidPostType(
            BoardCategory boardCategory,
            PostType postType
    ) {
        return Arrays.stream(PostType.values())
                .anyMatch(pt -> pt.category == boardCategory && pt == postType);
    }
}
