package com.example.notifications.service;

import com.example.notifications.dto.*;
import com.example.notifications.entity.Notification;
import com.example.notifications.exception.ApiException;
import com.example.notifications.repository.NotificationRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public NotificationResponse create(NotificationRequest r) {
        Notification n = repository.save(new Notification(r.userId(), r.orderId(), r.recipientEmail(), r.message()));
        log.info("Simulated notification sent: notificationId={}, userId={}, orderId={}, recipient={}", n.getId(), n.getUserId(), n.getOrderId(), n.getRecipientEmail());
        return NotificationResponse.from(n);
    }

    @Transactional(readOnly = true)
    public NotificationResponse get(long id) {
        return NotificationResponse.from(repository.findById(id).orElseThrow(ApiException::notFound));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> list() {
        return repository.findAll().stream().map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> byUser(long userId) {
        return repository.findByUserId(userId).stream().map(NotificationResponse::from).toList();
    }
}
