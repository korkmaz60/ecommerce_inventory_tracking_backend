package com.ecommerce.ecommerce.order.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private String shippingAddress;
    private String orderStatus;
    private String paymentStatus;
    private String notes;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    @Data
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Long sellerId;
        private String sellerName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private BigDecimal commissionRate;
        private BigDecimal commissionAmount;
        private BigDecimal sellerAmount;
    }
}