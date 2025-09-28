package com.ecommerce.ecommerce.payment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime paymentDate;
}
