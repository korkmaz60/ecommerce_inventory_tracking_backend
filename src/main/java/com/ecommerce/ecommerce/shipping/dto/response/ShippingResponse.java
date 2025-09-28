package com.ecommerce.ecommerce.shipping.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShippingResponse {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private String trackingNumber;
    private String shippingAddress;
    private String shippingMethod;
    private String carrier;
    private String status;
    private BigDecimal shippingCost;
    private LocalDateTime shippingDate;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String specialInstructions;
    private boolean signatureRequired;
    private boolean insuranceRequired;
    private String deliveryNotes;
    private String currentLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}