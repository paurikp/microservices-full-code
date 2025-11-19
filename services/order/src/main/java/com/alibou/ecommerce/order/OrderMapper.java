package com.alibou.ecommerce.order;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderMapper {

	public Order toOrder(OrderRequest request, BigDecimal orderAmount) {
		if (request == null) {
			return null;
		}
		return Order.builder()
				.id(request.id())
				.reference(request.reference())
				.paymentMethod(request.paymentMethod())
				.customerId(request.customerId())
                .totalAmount(orderAmount)
				.build();
	}

	public OrderResponse fromOrder(Order order) {
		return new OrderResponse(
				order.getId(), 
				order.getReference(), 
				order.getTotalAmount(), 
				order.getPaymentMethod(),
				order.getCustomerId());
	}
}
