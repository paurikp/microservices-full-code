package com.alibou.ecommerce.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibou.ecommerce.customer.CustomerClient;
import com.alibou.ecommerce.exception.BusinessException;
import com.alibou.ecommerce.kafka.OrderConfirmation;
import com.alibou.ecommerce.kafka.OrderProducer;
import com.alibou.ecommerce.orderline.OrderLineRequest;
import com.alibou.ecommerce.orderline.OrderLineService;
import com.alibou.ecommerce.payment.PaymentClient;
import com.alibou.ecommerce.payment.PaymentRequest;
import com.alibou.ecommerce.product.ProductClient;
import com.alibou.ecommerce.product.PurchaseResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository repository;
	private final OrderMapper mapper;
	private final CustomerClient customerClient;
	private final PaymentClient paymentClient;
	private final ProductClient productClient;
	private final OrderLineService orderLineService;
	private final OrderProducer orderProducer;

	@Transactional
	public Integer createOrder(OrderRequest request) {
		var customer = this.customerClient.findCustomerById(request.customerId()).orElseThrow(
				() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

		var purchasedProducts = productClient.purchaseProducts(request.products());

		var orderAmount = request.amount();

		if (orderAmount.equals(BigDecimal.valueOf(-1))) {
			orderAmount = purchasedProducts.stream().map(p -> p.price().multiply(BigDecimal.valueOf(p.quantity())))
					.reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
			if (orderAmount.compareTo(BigDecimal.ZERO) < 0) {
				throw new BusinessException("Cannot create order with zero total amount");
			}
		}

		var order = this.repository.save(mapper.toOrder(request, orderAmount));

//		for (PurchaseRequest purchaseRequest : request.products()) {
//			orderLineService.saveOrderLine(new OrderLineRequest(null, order.getId(), purchaseRequest.productId(),
//					purchaseRequest.quantity()));
//		}
		for (PurchaseResponse purchaseResponse : purchasedProducts) {

			var orderLinePrice = purchaseResponse.price().multiply(BigDecimal.valueOf(purchaseResponse.quantity()))
					.setScale(2, RoundingMode.HALF_UP);

			orderLineService.saveOrderLine(new OrderLineRequest(null, order.getId(), purchaseResponse.productId(),
					purchaseResponse.quantity(), orderLinePrice));
		}
		var paymentRequest = new PaymentRequest(orderAmount, request.paymentMethod(), order.getId(),
				order.getReference(), customer, request.paymentDetails());
		Integer paymentId = paymentClient.requestOrderPayment(paymentRequest);

		orderProducer.sendOrderConfirmation(new OrderConfirmation(request.reference(), orderAmount,
				request.paymentMethod(), customer, purchasedProducts));

		return order.getId();
	}

	public List<OrderResponse> findAllOrders() {
		return this.repository.findAll().stream().map(this.mapper::fromOrder).collect(Collectors.toList());
	}

	public OrderResponse findById(Integer id) {
		return this.repository.findById(id).map(this.mapper::fromOrder).orElseThrow(
				() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
	}
}
