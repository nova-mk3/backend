package org.nova.backend.board.util;

import java.util.UUID;

public final class FileCacheKeyConstants {
    public static final String IMAGE_META_PREFIX = "image:meta:";
    public static final String UPLOAD_STATUS_PREFIX = "upload:";

    private FileCacheKeyConstants() {
        throw new UnsupportedOperationException("상수 클래스는 인스턴스화할 수 없습니다.");
    }

    public static String imageMetaKey(UUID fileId) {
        return IMAGE_META_PREFIX + fileId;
    }

    public static String uploadStatusKey(UUID fileId) {
        return UPLOAD_STATUS_PREFIX + fileId;
    }
}