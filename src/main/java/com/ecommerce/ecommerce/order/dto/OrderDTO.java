package com.ecommerce.ecommerce.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
}
