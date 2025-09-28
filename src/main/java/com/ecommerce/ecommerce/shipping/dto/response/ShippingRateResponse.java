package com.ecommerce.ecommerce.shipping.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShippingRateResponse {
    private List<ShippingRate> rates;
    private String originAddress;
    private String destinationAddress;
    private LocalDateTime calculatedAt;
    
    @Data
    public static class ShippingRate {
        private String carrier;
        private String serviceName;
        private String serviceCode;
        private BigDecimal rate;
        private String currency;
        private Integer estimatedDays;
        private LocalDateTime estimatedDeliveryDate;
        private boolean signatureRequired;
        private boolean insuranceIncluded;
        private String description;
    }
}