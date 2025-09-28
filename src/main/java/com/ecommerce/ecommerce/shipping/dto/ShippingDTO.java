package com.ecommerce.ecommerce.shipping.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShippingDTO {
    private Long id;
    private Long orderId;
    private String trackingNumber;
    private String shippingAddress;
    private String status;
    private LocalDateTime shippingDate;
    private LocalDateTime estimatedDeliveryDate;
}
