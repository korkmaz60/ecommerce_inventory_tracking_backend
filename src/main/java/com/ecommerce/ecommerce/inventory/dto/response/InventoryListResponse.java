package com.ecommerce.ecommerce.inventory.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class InventoryListResponse {
    private List<InventorySummary> inventories;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private InventoryStats stats;
    
    @Data
    public static class InventorySummary {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private Integer currentQuantity;
        private Integer availableQuantity;
        private String location;
        private String status;
        private boolean lowStock;
    }
    
    @Data
    public static class InventoryStats {
        private int totalProducts;
        private int lowStockProducts;
        private int outOfStockProducts;
        private long totalInventoryValue;
    }
}