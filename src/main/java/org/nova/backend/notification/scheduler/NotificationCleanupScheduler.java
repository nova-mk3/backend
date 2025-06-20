package org.nova.backend.notification.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.nova.backend.notification.adapter.persistence.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationCleanupScheduler.class);
    private final NotificationRepository notificationRepository;

    /**
     * 매일 새벽 3시에 읽은 알림 중 6개월 이상 지난 것 삭제
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldReadNotifications() {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(6);
        int deleted = notificationRepository.deleteOldReadNotifications(threshold);
        if (deleted > 0) {
            log.info("[알림 정리 배치] {}개의 읽은 알림 삭제 완료", deleted);
        }
    }
}
