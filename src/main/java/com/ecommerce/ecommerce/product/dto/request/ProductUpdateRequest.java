package com.ecommerce.ecommerce.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NOT: Fiyat ve stok bilgileri ayrı modüllerde güncellenir (Pricing ve Inventory)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    private String name;

    private String description;

    private String sku;

    private Long categoryId;

    private String imageUrl;

    private Boolean active;
}