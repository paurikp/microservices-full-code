package com.alibou.ecommerce.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(Integer id, BigDecimal amount, PaymentMethod paymentMethod, Integer orderId,
                              String paymentDetails, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
}
