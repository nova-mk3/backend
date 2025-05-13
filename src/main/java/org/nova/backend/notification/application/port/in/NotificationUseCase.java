package org.nova.backend.notification.application.port.in;

import org.springframework.data.domain.Pageable;

import java.util.UUID;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.notification.application.dto.response.NotificationResponse;
import org.nova.backend.notification.domain.model.entity.Notification;
import org.nova.backend.notification.domain.model.entity.valueobject.EventType;
import org.springframework.data.domain.Page;

public interface NotificationUseCase {
    Notification create(UUID receiver, EventType eventType, UUID targetId, PostType targetType, String actorName);
    Page<NotificationResponse> getNotifications(UUID receiverId, Pageable pageable);
    long countUnread(UUID receiverId);
    void markAsRead(UUID notificationId, UUID receiverId);
}
