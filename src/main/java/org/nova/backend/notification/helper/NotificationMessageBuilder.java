package org.nova.backend.notification.helper;

import org.nova.backend.notification.domain.model.entity.valueobject.EventType;

public final class NotificationMessageBuilder {

    private NotificationMessageBuilder() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String build(EventType eventType, String actorName) {
        return switch (eventType) {
            case COMMENT -> actorName + "님이 댓글을 남겼습니다.";
            case REPLY -> actorName + "님이 대댓글을 남겼습니다.";
            case SUGGESTION_ANSWERED -> "관리자가 답글을 남겼습니다.";
            case POST_LIKE -> actorName + "님이 좋아요를 눌렀습니다.";
            case NOTIFICATION -> "공지사항이 달렸습니다.";
        };
    }
}
