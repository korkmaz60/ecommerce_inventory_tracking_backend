package com.ecommerce.ecommerce.inventory.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class InventoryUpdateRequest {
    @NotNull(message = "Ürün ID zorunludur")
    private Long productId;
    
    @NotNull(message = "Miktar zorunludur")
    @Min(value = 0, message = "Miktar negatif olamaz")
    private Integer quantity;
    
    private String location;
    private String reason;
    private String notes;
}