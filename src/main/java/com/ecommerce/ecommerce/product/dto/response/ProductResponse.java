package com.ecommerce.ecommerce.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private String sku;

    private Long categoryId;

    private String categoryName;

    private Long sellerId;

    private String sellerName;

    private String imageUrl;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String currency;

    private Integer stock;

    private Boolean inStock;

    private Boolean active;

    private LocalDateTime createdAt;
}