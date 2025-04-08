package org.nova.backend.shared.constants;

public class BoardErrorMessages {
    private BoardErrorMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String POST_NOT_FOUND = "게시글을 찾을 수 없습니다.";
    public static final String INVALID_BOARD_ID = "잘못된 게시판 ID입니다.";
    public static final String NO_AUTHORITY = "게시글 수정 권한이 없습니다.";
}
