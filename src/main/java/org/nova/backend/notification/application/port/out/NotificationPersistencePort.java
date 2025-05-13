package org.nova.backend.notification.application.port.out;

import org.nova.backend.notification.domain.model.entity.Notification;

public interface NotificationPersistencePort {
    Notification save(Notification notification);
}
