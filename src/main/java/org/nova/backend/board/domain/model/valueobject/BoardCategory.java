package org.nova.backend.board.domain.model.valueobject;

import lombok.Getter;

@Getter
public enum BoardCategory {
    INTEGRATED("통합 게시판"),
    CLUB_ARCHIVE("동아리 아카이브"),
    SUGGESTION("건의 게시판");

    private final String displayName;

    BoardCategory(String displayName) {
        this.displayName = displayName;
    }
}