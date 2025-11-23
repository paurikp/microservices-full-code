package com.alibou.ecommerce.notification;

import com.alibou.ecommerce.kafka.order.OrderConfirmation;
import com.alibou.ecommerce.kafka.payment.PaymentConfirmation;

import java.time.LocalDateTime;

public record NotificationResponse(String id, NotificationType type, LocalDateTime notificationDate,
                                   OrderConfirmation orderConfirmation, PaymentConfirmation paymentConfirmation) {
}
