package com.alibou.ecommerce.payment;

import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {

	public Payment toPayment(PaymentRequest request) {
		if (request == null) {
			return null;
		}
		return Payment.builder().id(request.id()).paymentMethod(request.paymentMethod()).amount(request.amount())
				.paymentDetails(request.paymentDetails()).orderId(request.orderId()).build();
	}

	public PaymentResponse toResponse(Payment payment) {
		if (payment == null) {
			return null;
		}
		return new PaymentResponse(payment.getId(), payment.getAmount(), payment.getPaymentMethod(), payment.getOrderId(),
				payment.getPaymentDetails(), payment.getCreatedDate(), payment.getLastModifiedDate());
	}
}