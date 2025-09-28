package com.ecommerce.ecommerce.shipping.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class ShippingCreateRequest {
    @NotBlank(message = "Kargo adresi zorunludur")
    private String shippingAddress;
    
    @NotBlank(message = "Kargo yöntemi zorunludur")
    private String shippingMethod; // STANDARD, EXPRESS, OVERNIGHT
    
    @NotBlank(message = "Kargo şirketi zorunludur")
    private String carrier; // UPS, FEDEX, DHL, etc.
    
    private BigDecimal shippingCost;
    private String specialInstructions;
    private boolean signatureRequired;
    private boolean insuranceRequired;
}