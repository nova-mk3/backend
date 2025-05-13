package org.nova.backend.notification.adapter.persistence.repository;

import java.util.UUID;
import org.nova.backend.notification.domain.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
