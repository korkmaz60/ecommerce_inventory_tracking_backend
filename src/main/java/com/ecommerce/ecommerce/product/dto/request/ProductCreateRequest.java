package com.ecommerce.ecommerce.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NOT: Fiyat ve stok bilgileri ayrı modüllerde tutulur (Pricing ve Inventory)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "Ürün adı zorunludur")
    private String name;

    private String description;

    @NotBlank(message = "SKU zorunludur")
    private String sku;

    @NotNull(message = "Kategori ID'si zorunludur")
    private Long categoryId;

    private Long sellerId;

    private String imageUrl;

    private Boolean active;
}