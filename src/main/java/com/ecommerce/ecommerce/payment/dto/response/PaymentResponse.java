package com.ecommerce.ecommerce.payment.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String transactionId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String gatewayResponse;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
}