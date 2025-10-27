package com.ecommerce.ecommerce.pricing.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class PriceUpdateRequest {

    private BigDecimal currentPrice; // Güncel fiyat

    private BigDecimal discountPrice; // İndirimli fiyat

    private BigDecimal discountPercentage; // İndirim yüzdesi

    private LocalDateTime discountStartDate; // İndirim başlangıç tarihi

    private LocalDateTime discountEndDate; // İndirim bitiş tarihi

    private String currency; // Para birimi

    private String changeReason; // Fiyat değişiklik sebebi (geçmiş kaydı için)
}
