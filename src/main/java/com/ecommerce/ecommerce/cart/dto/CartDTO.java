package com.ecommerce.ecommerce.cart.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private boolean active;
}
