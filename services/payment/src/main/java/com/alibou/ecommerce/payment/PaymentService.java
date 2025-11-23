package com.alibou.ecommerce.payment;

import com.alibou.ecommerce.notification.NotificationProducer;
import com.alibou.ecommerce.notification.PaymentNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final NotificationProducer notificationProducer;

    public Integer createPayment(PaymentRequest request) {
        var payment = this.repository.save(this.mapper.toPayment(request));

        this.notificationProducer.sendNotification(
                new PaymentNotificationRequest(request.orderReference(), request.amount(), request.paymentMethod(),
                        request.customer().firstname(), request.customer().lastname(), request.customer().email()));
        return payment.getId();
    }

    public PaymentResponse getPaymentById(Integer id) {
        var payment = this.repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for id: " + id));
        return this.mapper.toResponse(payment);
    }

    public List<PaymentResponse> getAllPayments() {
        return this.repository.findAll().stream().map(this.mapper::toResponse).collect(Collectors.toList());
    }

    public PaymentResponse updatePayment(Integer id, PaymentRequest request) {
        var existing = this.repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for id: " + id));

        // copy mutable fields from request
        existing.setAmount(request.amount());
        existing.setPaymentMethod(request.paymentMethod());
        existing.setOrderId(request.orderId());
        existing.setPaymentDetails(request.paymentDetails());

        var updated = this.repository.save(existing);
        return this.mapper.toResponse(updated);
    }

    public void deletePayment(Integer id) {
        if (!this.repository.existsById(id)) {
            throw new IllegalArgumentException("Payment not found for id: " + id);
        }
        this.repository.deleteById(id);
    }
}