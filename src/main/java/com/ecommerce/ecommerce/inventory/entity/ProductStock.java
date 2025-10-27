package com.ecommerce.ecommerce.inventory.entity;

import com.ecommerce.ecommerce.product.entity.Product;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_stocks")
public class ProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "min_stock_level")
    private Integer minStockLevel = 0; // Minimum stok seviyesi (kritik uyarı için)

    @Column(name = "max_stock_level")
    private Integer maxStockLevel; // Maksimum stok kapasitesi

    @Column(length = 100)
    private String warehouseLocation; // Depo lokasyonu ("A-Raf-3", "B-Koridor-5" --önemli değil de aslında yine de dursun duruma göre kaldırırım)

    @Column(nullable = false)
    private Boolean inStock = true; // Stokta var mı? (hızlı kontrol için)

    @OneToMany(mappedBy = "productStock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockMovement> stockMovements = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        updateInStockStatus();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateInStockStatus();
    }

    // Helper method - Stok durumunu güncelle
    private void updateInStockStatus() {
        this.inStock = this.quantity > 0;
    }

    // Helper method - Kritik stok seviyesi kontrolü
    public boolean isLowStock() {
        return quantity <= minStockLevel;
    }

    // Helper method - Stok ekleme
    public void addStock(Integer amount) {
        this.quantity += amount;
    }

    // Helper method - Stok azaltma
    public void reduceStock(Integer amount) throws IllegalArgumentException {
        if (this.quantity < amount) {
            throw new IllegalArgumentException("Yetersiz stok! Mevcut: " + this.quantity + ", İstenen: " + amount);
        }
        this.quantity -= amount;
    }

    // Constructors
    public ProductStock() {
    }

    public ProductStock(Product product, Integer quantity, Integer minStockLevel) {
        this.product = product;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
    }

    // Getters ve Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public Integer getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(Integer maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public List<StockMovement> getStockMovements() {
        return stockMovements;
    }

    public void setStockMovements(List<StockMovement> stockMovements) {
        this.stockMovements = stockMovements;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
