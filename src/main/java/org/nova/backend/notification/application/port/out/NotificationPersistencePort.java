package org.nova.backend.notification.application.port.out;

import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.notification.domain.model.entity.Notification;
import org.springframework.data.domain.Page;

public interface NotificationPersistencePort {
    Notification save(Notification notification);
    Page<Notification> findByReceiver(UUID receiverId, Pageable pageable);
    Page<Notification> findUnreadByReceiver(UUID receiverId, Pageable pageable);
    Optional<Notification> findById(UUID id);
    int countByReceiverAndIsReadFalse(UUID receiverId);
    int markAsRead(UUID notificationId, UUID receiverId);
    int markAllAsRead(UUID receiverId);
}
