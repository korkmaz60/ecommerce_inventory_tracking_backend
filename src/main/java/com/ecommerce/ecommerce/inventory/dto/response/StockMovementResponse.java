package com.ecommerce.ecommerce.inventory.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockMovementResponse {
    private List<StockMovement> movements;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    
    @Data
    public static class StockMovement {
        private Long id;
        private Long productId;
        private String productName;
        private String movementType; // IN, OUT, ADJUSTMENT, TRANSFER
        private Integer quantity;
        private Integer previousQuantity;
        private Integer newQuantity;
        private String reason;
        private String notes;
        private String location;
        private String performedBy;
        private LocalDateTime createdAt;
    }
}