package org.nova.backend.notification.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.notification.domain.model.entity.valueobject.EventType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID uuid;
    private boolean isRead;
    private EventType eventType;
    private String message;
    private UUID targetId;
    private PostType targetType;
    private LocalDateTime createdTime;
}