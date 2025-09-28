package com.ecommerce.ecommerce.shipping.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class ShippingRateRequest {
    @NotBlank(message = "Gönderen adresi zorunludur")
    private String originAddress;
    
    @NotBlank(message = "Alıcı adresi zorunludur")
    private String destinationAddress;
    
    @NotNull(message = "Paket ağırlığı zorunludur")
    @Positive(message = "Ağırlık pozitif olmalıdır")
    private BigDecimal weight;
    
    @NotNull(message = "Paket boyutları zorunludur")
    private PackageDimensions dimensions;
    
    private String shippingMethod;
    private BigDecimal declaredValue;
    private boolean signatureRequired;
    private boolean insuranceRequired;
    
    @Data
    public static class PackageDimensions {
        @Positive(message = "Uzunluk pozitif olmalıdır")
        private BigDecimal length;
        
        @Positive(message = "Genişlik pozitif olmalıdır")
        private BigDecimal width;
        
        @Positive(message = "Yükseklik pozitif olmalıdır")
        private BigDecimal height;
    }
}