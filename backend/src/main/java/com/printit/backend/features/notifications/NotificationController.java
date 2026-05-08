package com.printit.backend.features.notifications;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> getNotifications(@RequestParam String email) {
        return notificationService.getNotificationsByEmail(email);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(@RequestParam String email) {
        return Map.of("count", notificationService.getUnreadCountByEmail(email));
    }

    @PutMapping("/{notificationId}/read")
    public Map<String, String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return Map.of("message", "Notification marked as read.");
    }
}