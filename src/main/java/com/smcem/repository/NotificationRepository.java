package com.smcem.repository;

import com.smcem.entity.Notification;
import com.smcem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverAndIsRead(User receiver, boolean isRead);

    List<Notification> findByReceiver(User receiver);

    long countByReceiverAndIsRead(User receiver, boolean isRead);
}