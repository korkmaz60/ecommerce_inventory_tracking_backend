package com.ecommerce.ecommerce.pricing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceResponse {

    private Long id;

    private BigDecimal currentPrice;

    private BigDecimal discountPrice;

    private BigDecimal discountPercentage;

    private LocalDateTime discountStartDate;

    private LocalDateTime discountEndDate;

    private String currency;

    private BigDecimal effectivePrice; // Geçerli fiyat (indirimliyse indirimli, değilse normal)

    private Boolean hasActiveDiscount; // Aktif indirimi var mı

    private LocalDateTime updatedAt;
}
