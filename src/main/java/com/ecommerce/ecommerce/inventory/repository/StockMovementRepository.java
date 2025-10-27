package com.ecommerce.ecommerce.inventory.repository;

import com.ecommerce.ecommerce.inventory.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    // Ürün stok ID'sine göre hareketleri getirir (sayfalı)
    Page<StockMovement> findByProductStockIdOrderByCreatedAtDesc(Long productStockId, Pageable pageable);

    // Ürün stok ID'sine göre hareketleri getirir (liste)
    List<StockMovement> findByProductStockIdOrderByCreatedAtDesc(Long productStockId);

    // Hareket tipine göre getirir
    List<StockMovement> findByMovementTypeOrderByCreatedAtDesc(StockMovement.MovementType movementType);

    // Belirli bir tarih aralığındaki hareketleri getirir
    List<StockMovement> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    // Belirli bir kullanıcının yaptığı hareketleri getirir
    List<StockMovement> findByPerformedByOrderByCreatedAtDesc(String performedBy);

    // Referans ID'ye göre hareket bulur (sipariş, tedarik vb.)
    List<StockMovement> findByReferenceId(String referenceId);

    // Son N hareketi getirir
    @Query("SELECT sm FROM StockMovement sm ORDER BY sm.createdAt DESC")
    Page<StockMovement> findLatestMovements(Pageable pageable);
}
