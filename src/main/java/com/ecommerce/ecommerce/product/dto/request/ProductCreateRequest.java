package com.ecommerce.ecommerce.product.dto.request;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "Ürün adı zorunludur")
    private String name;
    
    @NotBlank(message = "Ürün açıklaması zorunludur")
    private String description;
    
    @NotNull(message = "Fiyat zorunludur")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fiyat 0'dan büyük olmalıdır")
    private BigDecimal price;
    
    @NotNull(message = "Stok miktarı zorunludur")
    @Min(value = 0, message = "Stok miktarı negatif olamaz")
    private Integer stock;
    
    @NotBlank(message = "Kategori zorunludur")
    private String category;
    
    private String imageUrl;
    private String brand;
    private String sku;
}