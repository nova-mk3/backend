package org.nova.backend.notification.adapter.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
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
    private final MemberRepository memberRepository;

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
    public void markAsRead(UUID notificationId, UUID receiverId) {
        notificationRepository.markAsRead(notificationId, receiverId);
    }

    @Override
    public long countByReceiverAndIsReadFalse(UUID receiverId) {
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return notificationRepository.countByReceiver_IdAndIsReadFalse(receiver);
    }
}
