package com.taskmaster_springboot.service;

import com.taskmaster_springboot.dto.response.NotificationResponseDTO;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void sendNotification(UUID userId, String type, String message);
    List<NotificationResponseDTO> getUserNotifications(String email);
    void markNotificationAsRead(UUID notificationId);
    void markAllNotificationsAsRead(String email);
    void deleteNotification(UUID notificationId);
}
