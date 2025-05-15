package org.nova.backend.notification.adapter.web;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.notification.adapter.doc.NotificationApiDocument;
import org.nova.backend.notification.application.dto.response.NotificationResponse;
import org.nova.backend.notification.application.dto.response.UnreadCountResponse;
import org.nova.backend.notification.application.port.in.NotificationUseCase;
import org.nova.backend.shared.model.ApiResponse;
import org.nova.backend.board.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Notification API", description = "알림 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationUseCase notificationUseCase;
    private final SecurityUtil securityUtil;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @NotificationApiDocument.GetNotifications
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(Pageable pageable) {
        UUID receiverId = securityUtil.getCurrentMemberId();
        var result = notificationUseCase.getNotifications(receiverId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unread")
    @NotificationApiDocument.GetUnreadNotifications
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUnreadNotifications(
            @Parameter(hidden = true) Pageable pageable
    ) {
        UUID receiverId = securityUtil.getCurrentMemberId();
        var result = notificationUseCase.getUnreadNotifications(receiverId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unread-count")
    @NotificationApiDocument.CountUnread
    public ResponseEntity<ApiResponse<UnreadCountResponse>> countUnread() {
        UUID receiverId = securityUtil.getCurrentMemberId();
        UnreadCountResponse unreadCountResponse = notificationUseCase.countUnread(receiverId);
        return ResponseEntity.ok(ApiResponse.success(unreadCountResponse));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{notificationId}/read")
    @NotificationApiDocument.MarkAsRead
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID notificationId) {
        UUID receiverId = securityUtil.getCurrentMemberId();
        notificationUseCase.markAsRead(notificationId, receiverId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/read-all")
    @NotificationApiDocument.MarkAllAsRead
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        UUID receiverId = securityUtil.getCurrentMemberId();
        notificationUseCase.markAllAsRead(receiverId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}