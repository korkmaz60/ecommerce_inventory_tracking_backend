package com.ecommerce.ecommerce.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Stok bilgilerini döndürmek için kullanılan DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {

    private Long id;

    private Integer quantity;

    private Integer minStockLevel;

    private Integer maxStockLevel;

    private String warehouseLocation;

    private Boolean inStock;

    private Boolean isLowStock; // Kritik stok seviyesinde mi

    private LocalDateTime updatedAt;
}
