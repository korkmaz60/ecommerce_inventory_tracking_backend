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
public class PriceHistoryResponse {

    private Long id;

    private BigDecimal oldPrice;

    private BigDecimal newPrice;

    private String changeReason;

    private String changedBy;

    private LocalDateTime changedAt;
}
