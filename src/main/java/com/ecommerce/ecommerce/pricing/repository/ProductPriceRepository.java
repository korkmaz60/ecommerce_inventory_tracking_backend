package com.ecommerce.ecommerce.pricing.repository;

import com.ecommerce.ecommerce.pricing.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {

    Optional<ProductPrice> findByProductId(Long productId);

    // Aktif indirimi olan ürünleri getir
    @Query("SELECT pp FROM ProductPrice pp WHERE pp.discountStartDate <= CURRENT_TIMESTAMP " +
           "AND pp.discountEndDate >= CURRENT_TIMESTAMP AND pp.discountPrice IS NOT NULL")
    List<ProductPrice> findProductsWithActiveDiscount();

    // Belirli bir para birimindeki fiyatları getir
    List<ProductPrice> findByCurrency(String currency);
}
