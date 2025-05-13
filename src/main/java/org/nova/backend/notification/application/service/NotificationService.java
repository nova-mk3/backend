package org.nova.backend.notification.application.service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.UUID;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.notification.application.dto.response.NotificationResponse;
import org.nova.backend.notification.application.mapper.NotificationMapper;
import org.nova.backend.notification.domain.exception.NotificationDomainException;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService implements NotificationUseCase {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationPersistencePort notificationPersistencePort;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Notification create(
            UUID receiverId,
            EventType eventType,
            UUID targetId,
            PostType targetType,
            String actorName
    ) {

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

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

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(
            UUID receiver,
            Pageable pageable
    ) {
        return notificationPersistencePort.findByReceiver(receiver, pageable)
                .map(NotificationMapper::toResponse);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, UUID receiverId) {
        Notification notification = notificationPersistencePort.findById(notificationId)
                .orElseThrow(() -> new NotificationDomainException("알림이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        if (!notification.getReceiver().getId().equals(receiverId)) {
            throw new NotificationDomainException("해당 알림에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        notificationPersistencePort.markAsRead(notificationId, receiverId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(UUID receiverId) {
        return notificationPersistencePort.countByReceiverAndIsReadFalse(receiverId);
    }
}
