package edu.icet.controller;

import edu.icet.model.entity.NotificationEntity;
import edu.icet.model.entity.User;
import edu.icet.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<?> getNotifications(Authentication authentication) {
        User user = currentUser(authentication);

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized request."));
        }

        List<NotificationEntity> notifications =
                notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());

        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/read")
    public ResponseEntity<?> markNotificationsRead(Authentication authentication) {
        User user = currentUser(authentication);

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized request."));
        }

        List<NotificationEntity> notifications =
                notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());

        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);

        return ResponseEntity.ok(Map.of("message", "Notifications marked as read."));
    }

    private User currentUser(Authentication authentication) {
        return authentication != null && authentication.getPrincipal() instanceof User user ? user : null;
    }
}
