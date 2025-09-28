package com.ecommerce.ecommerce.cart.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class CartUpdateItemRequest {
    @NotNull(message = "Miktar zorunludur")
    @Positive(message = "Miktar pozitif olmalıdır")
    private Integer quantity;
}