package com.alibou.ecommerce.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {

    public Notification toNotification(NotificationRequest request) {
        if (request == null) return null;
        return Notification.builder()
                .type(request.type())
                .notificationDate(request.notificationDate())
                .orderConfirmation(request.orderConfirmation())
                .paymentConfirmation(request.paymentConfirmation())
                .build();
    }

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) return null;
        return new NotificationResponse(notification.getId(), notification.getType(), notification.getNotificationDate(),
                notification.getOrderConfirmation(), notification.getPaymentConfirmation());
    }
}
