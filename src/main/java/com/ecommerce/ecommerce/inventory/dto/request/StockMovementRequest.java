package com.ecommerce.ecommerce.inventory.dto.request;

import com.ecommerce.ecommerce.inventory.entity.StockMovement;
import lombok.Data;

/**
 * Stok hareketi oluşturma işlemi için kullanılan DTO
 */
@Data
public class StockMovementRequest {

    private Long productId;

    private StockMovement.MovementType movementType; // Hareket tipi (IN/OUT)

    private Integer quantity;

    private String reason; // Hareket sebebi

    private String referenceId; // İlgili sipariş/tedarik ID'si
}
