package com.ecommerce.ecommerce.inventory.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer currentQuantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private String location;
    private String warehouseCode;
    private String status;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;
}