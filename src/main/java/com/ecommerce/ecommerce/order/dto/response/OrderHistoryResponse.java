package com.ecommerce.ecommerce.order.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderHistoryResponse {
    private List<OrderSummary> orders;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    
    @Data
    public static class OrderSummary {
        private Long id;
        private String orderNumber;
        private LocalDateTime orderDate;
        private String status;
        private BigDecimal totalAmount;
        private Integer itemCount;
        private String trackingNumber;
    }
}