package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.dto.response.NotificationResponseDTO;
import com.taskmaster_springboot.exceptions.AuthenticationException;
import com.taskmaster_springboot.exceptions.ResourceNotFoundException;
import com.taskmaster_springboot.model.Notifications;
import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.model.enums.NotificationType;
import com.taskmaster_springboot.repository.NotificationRepository;
import com.taskmaster_springboot.repository.UsersRepository;
import com.taskmaster_springboot.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UsersRepository usersRepository;

    @Override
    public void sendNotification(UUID userId, String type, String message) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notifications notification = Notifications.builder()
                .user(user)
                .type(NotificationType.valueOf(type))
                .payload(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("Notification sent to user: {}", userId);
    }

    @Override
    public List<NotificationResponseDTO> getUserNotifications(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        return notificationRepository.findByUserId(user.getUserId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void markNotificationAsRead(UUID notificationId) {
        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setIsRead(true);
        notificationRepository.save(notification);
        log.info("Notification marked as read: {}", notificationId);
    }

    @Override
    public void markAllNotificationsAsRead(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        List<Notifications> notifications = notificationRepository.findByUserId(user.getUserId());
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
        log.info("All notifications marked as read for user: {}", user.getUserId());
    }

    @Override
    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
        log.info("Notification deleted: {}", notificationId);
    }

    private NotificationResponseDTO mapToDTO(Notifications notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getUserId())
                .type(String.valueOf(notification.getType()))
                .payload(notification.getPayload())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
