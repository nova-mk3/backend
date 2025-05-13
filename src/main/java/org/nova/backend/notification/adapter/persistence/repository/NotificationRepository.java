package org.nova.backend.notification.adapter.persistence.repository;

import org.springframework.data.repository.query.Param;
import java.util.UUID;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.notification.domain.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByReceiver_IdOrderByCreatedTimeDesc(UUID receiverId, Pageable pageable);
    long countByReceiver_IdAndIsReadFalse(Member receiver);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.receiver.id = :receiverId")
    int markAsRead(@Param("id") UUID id, @Param("receiverId") UUID receiverId);
}
