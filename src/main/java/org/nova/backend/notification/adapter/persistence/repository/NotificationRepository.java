package org.nova.backend.notification.adapter.persistence.repository;

import java.time.LocalDateTime;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
import org.nova.backend.notification.domain.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByReceiver_IdOrderByCreatedTimeDesc(UUID receiverId, Pageable pageable);
    Page<Notification> findByReceiver_IdAndIsReadFalseOrderByCreatedTimeDesc(UUID receiverId, Pageable pageable);

    int countByReceiver_IdAndIsReadFalse(UUID receiverId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.receiver.id = :receiverId")
    int markAsRead(@Param("id") UUID id, @Param("receiverId") UUID receiverId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver.id = :receiverId AND n.isRead = false")
    int markAllAsRead(@Param("receiverId") UUID receiverId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.isRead = true AND n.createdTime < :threshold")
    int deleteOldReadNotifications(@Param("threshold") LocalDateTime threshold);
}
