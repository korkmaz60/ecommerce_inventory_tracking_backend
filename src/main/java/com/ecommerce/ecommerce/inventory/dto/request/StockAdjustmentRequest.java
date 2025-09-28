package com.ecommerce.ecommerce.inventory.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class StockAdjustmentRequest {
    @NotNull(message = "Ürün ID zorunludur")
    private Long productId;
    
    @NotNull(message = "Düzeltme miktarı zorunludur")
    private Integer adjustmentQuantity; // Can be positive or negative
    
    @NotBlank(message = "Düzeltme türü zorunludur")
    private String adjustmentType; // INCREASE, DECREASE, CORRECTION
    
    @NotBlank(message = "Sebep zorunludur")
    private String reason;
    
    private String notes;
    private String location;
}