package com.ecommerce.ecommerce.product.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private String imageUrl;
    private String brand;
    private String sku;
    private boolean active;
    private List<String> images;
    private List<ProductAttribute> attributes;
    private Double averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class ProductAttribute {
        private String name;
        private String value;
    }
}