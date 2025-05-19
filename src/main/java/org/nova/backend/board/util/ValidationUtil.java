package org.nova.backend.board.util;

import org.nova.backend.board.common.domain.exception.BoardDomainException;

public class ValidationUtil {

    private ValidationUtil() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static void requireNonBlank(
            String value,
            String fieldName
    ) {
        if (value == null || value.trim().isEmpty()) {
            throw new BoardDomainException(fieldName + "은 비어 있을 수 없습니다.");
        }
    }
}
