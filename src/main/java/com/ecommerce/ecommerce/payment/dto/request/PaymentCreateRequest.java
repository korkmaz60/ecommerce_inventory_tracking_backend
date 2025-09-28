package com.ecommerce.ecommerce.payment.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PaymentCreateRequest {
    @NotNull(message = "Tutar zorunludur")
    private BigDecimal amount;
    
    @NotBlank(message = "Ödeme yöntemi zorunludur")
    private String paymentMethod;
    
    private String cardToken;
    private String description;
}