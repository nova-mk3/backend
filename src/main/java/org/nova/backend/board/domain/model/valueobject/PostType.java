package org.nova.backend.board.domain.model.valueobject;

import lombok.Getter;

@Getter
public enum PostType {
    QNA("Q&A", BoardCategory.INTEGRATED),
    FREE("자유게시판", BoardCategory.INTEGRATED),
    INTRODUCTION("자기소개", BoardCategory.INTEGRATED),
    NOTICE("공지사항", BoardCategory.INTEGRATED),
    EXAM_ARCHIVE("족보 게시판", BoardCategory.CLUB_ARCHIVE),
    PICTURES("사진 게시판", BoardCategory.CLUB_ARCHIVE),
    SUGGESTION("건의 게시판", BoardCategory.SUGGESTION);

    private final String displayName;
    private final BoardCategory category;

    PostType(String displayName, BoardCategory category) {
        this.displayName = displayName;
        this.category = category;
    }
}
