package com.ecommerce.ecommerce.product.dto.request;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private String name;
    private String description;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Fiyat 0'dan büyük olmalıdır")
    private BigDecimal price;
    
    @Min(value = 0, message = "Stok miktarı negatif olamaz")
    private Integer stock;
    
    private String category;
    private String imageUrl;
    private String brand;
    private Boolean active;
}