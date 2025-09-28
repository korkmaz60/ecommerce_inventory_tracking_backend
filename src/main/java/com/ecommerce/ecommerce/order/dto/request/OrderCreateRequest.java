package com.ecommerce.ecommerce.order.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Data
public class OrderCreateRequest {
    @NotEmpty(message = "Sipariş kalemleri boş olamaz")
    private List<OrderItemRequest> items;

    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private String notes;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Ürün ID zorunludur")
        private Long productId;

        @NotNull(message = "Miktar zorunludur")
        @Positive(message = "Miktar pozitif olmalıdır")
        private Integer quantity;
    }
}