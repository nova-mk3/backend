package org.nova.backend.board.util;

import java.util.UUID;

public final class FileCacheKeyConstants {
    public static final String IMAGE_META_PREFIX = "image:meta:";

    private FileCacheKeyConstants() {
        throw new UnsupportedOperationException("상수 클래스는 인스턴스화할 수 없습니다.");
    }

    public static String imageMetaKey(UUID fileId) {
        return IMAGE_META_PREFIX + fileId;
    }
}