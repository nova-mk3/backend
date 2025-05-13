package org.nova.backend.notification.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.notification.adapter.doc.NotificationApiDocument;
import org.nova.backend.notification.application.dto.response.NotificationResponse;
import org.nova.backend.notification.application.port.in.NotificationUseCase;
import org.nova.backend.shared.model.ApiResponse;
import org.nova.backend.board.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Notification API", description = "알림 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationUseCase notificationUseCase;
    private final SecurityUtil securityUtil;

    @GetMapping
    @NotificationApiDocument.GetNotifications
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(Pageable pageable) {
        UUID receiverId = securityUtil.getCurrentMemberId();
        var result = notificationUseCase.getNotifications(receiverId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/unread-count")
    @NotificationApiDocument.CountUnread
    public ResponseEntity<ApiResponse<Long>> countUnread() {
        UUID receiverId = securityUtil.getCurrentMemberId();
        long count = notificationUseCase.countUnread(receiverId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PatchMapping("/{notificationId}/read")
    @NotificationApiDocument.MarkAsRead
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID notificationId) {
        UUID receiverId = securityUtil.getCurrentMemberId();
        notificationUseCase.markAsRead(notificationId, receiverId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}