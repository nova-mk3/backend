package org.nova.backend.notification.application.service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.notification.application.port.in.NotificationUseCase;
import org.nova.backend.notification.application.port.out.NotificationPersistencePort;
import org.nova.backend.notification.domain.model.entity.Notification;
import org.nova.backend.notification.domain.model.entity.valueobject.EventType;
import org.nova.backend.notification.helper.NotificationMessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService implements NotificationUseCase {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationPersistencePort notificationPersistencePort;

    @Override
    public Notification create(
            Member receiver,
            EventType eventType,
            UUID targetId,
            PostType targetType,
            String actorName
    ) {
        String message = NotificationMessageBuilder.build(eventType, actorName);
        if (message == null) {
            message = eventType.getMessageTemplate();
        }

        Notification notification = new Notification(
                UUID.randomUUID(),
                receiver,
                message,
                eventType,
                targetId,
                targetType,
                false,
                LocalDateTime.now()
        );

        return notificationPersistencePort.save(notification);
    }
}
