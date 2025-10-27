package com.ecommerce.ecommerce.inventory.repository;

import com.ecommerce.ecommerce.inventory.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {


    Optional<ProductStock> findByProductId(Long productId);


    List<ProductStock> findByInStockTrue();

    // Stokta olmayan ürünleri getir
    List<ProductStock> findByInStockFalse();

    // Kritik stok seviyesindeki ürünleri getir
    @Query("SELECT ps FROM ProductStock ps WHERE ps.quantity <= ps.minStockLevel AND ps.quantity > 0")
    List<ProductStock> findLowStockProducts();

    // Stokta olmayan ürünleri getir (quantity = 0)
    @Query("SELECT ps FROM ProductStock ps WHERE ps.quantity = 0")
    List<ProductStock> findOutOfStockProducts();

    // Belirli bir depodaki ürünleri getir
    List<ProductStock> findByWarehouseLocation(String warehouseLocation);
}
