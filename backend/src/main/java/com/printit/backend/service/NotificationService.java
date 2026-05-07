package com.printit.backend.service;

import com.printit.backend.dto.NotificationResponse;
import com.printit.backend.entity.Notification;
import com.printit.backend.entity.User;
import com.printit.backend.repository.NotificationRepository;
import com.printit.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void createNotification(User user, String title, String message, String type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getNotificationsByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public long getUnreadCountByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found."));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}