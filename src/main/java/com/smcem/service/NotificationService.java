package com.smcem.service;

import com.smcem.entity.Notification;
import com.smcem.entity.User;
import com.smcem.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification sendNotification(User sender, User receiver, String message) {
        Notification notification = new Notification(sender, receiver, message);
        return notificationRepository.save(notification);
    }

    public List<Notification> findByReceiver(User receiver) {
        return notificationRepository.findByReceiver(receiver);
    }

    public List<Notification> findUnreadByReceiver(User receiver) {
        return notificationRepository.findByReceiverAndIsRead(receiver, false);
    }

    public long countUnreadByReceiver(User receiver) {
        return notificationRepository.countByReceiverAndIsRead(receiver, false);
    }

    public void markAsRead(Long notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}