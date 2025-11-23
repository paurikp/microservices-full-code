package com.alibou.ecommerce.orderline;

import java.math.BigDecimal;

public record OrderLineRequest(
		Integer id, 
		Integer orderId, 
		Integer productId, 
		double quantity,
		BigDecimal price) {
}
