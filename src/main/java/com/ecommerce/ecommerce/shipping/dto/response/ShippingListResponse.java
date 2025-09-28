package com.ecommerce.ecommerce.shipping.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShippingListResponse {
    private List<ShippingSummary> shipments;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    
    @Data
    public static class ShippingSummary {
        private Long id;
        private Long orderId;
        private String orderNumber;
        private String trackingNumber;
        private String carrier;
        private String status;
        private BigDecimal shippingCost;
        private LocalDateTime shippingDate;
        private LocalDateTime estimatedDeliveryDate;
        private String customerName;
        private String shippingCity;
    }
}