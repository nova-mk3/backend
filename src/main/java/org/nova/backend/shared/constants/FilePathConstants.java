package org.nova.backend.shared.constants;

public final class FilePathConstants {

    private FilePathConstants() {
        throw new UnsupportedOperationException("상수 클래스는 인스턴스화할 수 없습니다.");
    }

    public static final String PUBLIC_FOLDER = "public";

    public static final String PROTECTED_FOLDER = "protected";

    public static final String PUBLIC_FILE_URL_PREFIX = "/files/public/";
    public static final String PROTECTED_FILE_URL_PREFIX = "/files/protected/";
}

