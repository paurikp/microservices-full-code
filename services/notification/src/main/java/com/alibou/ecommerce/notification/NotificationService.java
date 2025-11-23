package com.alibou.ecommerce.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    public String createNotification(NotificationRequest request) {
        var saved = this.repository.save(this.mapper.toNotification(request));
        return saved.getId();
    }

    public NotificationResponse getById(String id) {
        var n = this.repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
        return this.mapper.toResponse(n);
    }

    public List<NotificationResponse> getAll() {
        return this.repository.findAll().stream().map(this.mapper::toResponse).collect(Collectors.toList());
    }

    public NotificationResponse update(String id, NotificationRequest request) {
        var existing = this.repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
        existing.setType(request.type());
        existing.setNotificationDate(request.notificationDate());
        existing.setOrderConfirmation(request.orderConfirmation());
        existing.setPaymentConfirmation(request.paymentConfirmation());
        var updated = this.repository.save(existing);
        return this.mapper.toResponse(updated);
    }

    public void delete(String id) {
        if (!this.repository.existsById(id)) throw new IllegalArgumentException("Notification not found: " + id);
        this.repository.deleteById(id);
    }
}
