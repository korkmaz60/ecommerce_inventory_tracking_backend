package com.ecommerce.ecommerce.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Stok hareketi bilgilerini döndürmek için kullanılan DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {

    private Long id;

    private String movementType; // IN, OUT

    private Integer quantity;

    private Integer previousQuantity;

    private Integer newQuantity;

    private String reason;

    private String referenceId;

    private String performedBy;

    private LocalDateTime createdAt;
}