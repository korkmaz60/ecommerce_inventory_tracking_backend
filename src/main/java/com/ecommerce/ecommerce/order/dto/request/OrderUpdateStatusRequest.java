package com.ecommerce.ecommerce.order.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class OrderUpdateStatusRequest {
    @NotBlank(message = "Durum zorunludur")
    private String status;
    
    private String notes;
    private String trackingNumber;
}