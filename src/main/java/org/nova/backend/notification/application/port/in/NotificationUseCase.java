package org.nova.backend.notification.application.port.in;

import java.util.UUID;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.notification.domain.model.entity.Notification;
import org.nova.backend.notification.domain.model.entity.valueobject.EventType;

public interface NotificationUseCase {
    Notification create(Member receiver, EventType eventType, UUID targetId, PostType targetType, String actorName);
}
