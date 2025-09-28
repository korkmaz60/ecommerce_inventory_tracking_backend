package com.ecommerce.ecommerce.product.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSummaryResponse {
    private List<ProductSummary> products;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    
    @Data
    public static class ProductSummary {
        private Long id;
        private String name;
        private BigDecimal price;
        private Integer stock;
        private String category;
        private String imageUrl;
        private boolean active;
        private Double averageRating;
    }
}