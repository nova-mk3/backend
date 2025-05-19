package org.nova.backend.notification.adapter.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.notification.adapter.persistence.repository.NotificationRepository;
import org.nova.backend.notification.application.port.out.NotificationPersistencePort;
import org.nova.backend.notification.domain.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements NotificationPersistencePort {
    private final NotificationRepository notificationRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Page<Notification> findByReceiver(UUID receiverId, Pageable pageable) {
        return notificationRepository.findByReceiver_IdOrderByCreatedTimeDesc(receiverId, pageable);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return notificationRepository.findById(id);
    }

    @Override
    public int markAsRead(UUID notificationId, UUID receiverId) {
        return notificationRepository.markAsRead(notificationId, receiverId);
    }

    @Override
    public Page<Notification> findUnreadByReceiver(UUID receiverId, Pageable pageable) {
        return notificationRepository.findByReceiver_IdAndIsReadFalseOrderByCreatedTimeDesc(receiverId, pageable);
    }

    @Override
    public int countByReceiverAndIsReadFalse(UUID receiverId) {
        return notificationRepository.countByReceiver_IdAndIsReadFalse(receiverId);
    }

    @Override
    public int markAllAsRead(UUID receiverId) {
        return notificationRepository.markAllAsRead(receiverId);
    }
}
