package com.ecommerce.ecommerce.shipping.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShippingUpdateRequest {
    private String status; // PENDING, SHIPPED, IN_TRANSIT, DELIVERED, RETURNED
    private String trackingNumber;
    private LocalDateTime shippingDate;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String deliveryNotes;
    private String currentLocation;
}