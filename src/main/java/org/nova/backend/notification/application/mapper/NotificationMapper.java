package org.nova.backend.notification.application.mapper;

import org.nova.backend.notification.domain.model.entity.Notification;
import org.nova.backend.notification.application.dto.response.NotificationResponse;

public class NotificationMapper {

    private NotificationMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.isRead(),
                notification.getEventType(),
                notification.getMessage(),
                notification.getTargetId(),
                notification.getTargetType(),
                notification.getCreatedTime()
        );
    }
}
