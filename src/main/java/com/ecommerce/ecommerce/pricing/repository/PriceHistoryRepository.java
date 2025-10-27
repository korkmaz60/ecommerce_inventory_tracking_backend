package com.ecommerce.ecommerce.pricing.repository;

import com.ecommerce.ecommerce.pricing.entity.PriceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    // Ürün fiyat ID'sine göre geçmişi getirir (sayfalı)
    Page<PriceHistory> findByProductPriceIdOrderByChangedAtDesc(Long productPriceId, Pageable pageable);

    // Ürün fiyat ID'sine göre geçmişi getirir (liste)
    List<PriceHistory> findByProductPriceIdOrderByChangedAtDesc(Long productPriceId);

    // Belirli bir tarih aralığındaki değişiklikleri getirir
    List<PriceHistory> findByChangedAtBetweenOrderByChangedAtDesc(
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    // Belirli bir kullanıcının yaptığı değişiklikleri getirir
    List<PriceHistory> findByChangedByOrderByChangedAtDesc(String changedBy);
}
