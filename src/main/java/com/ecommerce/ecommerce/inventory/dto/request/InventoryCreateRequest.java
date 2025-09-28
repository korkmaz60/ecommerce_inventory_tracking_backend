package com.ecommerce.ecommerce.inventory.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class InventoryCreateRequest {
    @NotNull(message = "Ürün ID zorunludur")
    private Long productId;
    
    @NotNull(message = "Başlangıç miktarı zorunludur")
    @Min(value = 0, message = "Miktar negatif olamaz")
    private Integer initialQuantity;
    
    @NotBlank(message = "Konum zorunludur")
    private String location;
    
    @NotBlank(message = "Depo kodu zorunludur")
    private String warehouseCode;
    
    private Integer minStockLevel;
    private Integer maxStockLevel;
}