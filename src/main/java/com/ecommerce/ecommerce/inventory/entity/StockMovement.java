package com.ecommerce.ecommerce.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_stock_id", nullable = false)
    private ProductStock productStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType movementType; // IN (giriş) veya OUT (çıkış)

    @Column(nullable = false)
    private Integer quantity; // Miktar

    @Column(name = "previous_quantity", nullable = false)
    private Integer previousQuantity; // İşlemden önceki stok

    @Column(name = "new_quantity", nullable = false)
    private Integer newQuantity; // İşlemden sonraki stok

    @Column(length = 500)
    private String reason; // Hareket sebebi ("Satış", "İade", "Yeni tedarik", "Sayım düzeltmesi")

    @Column(name = "reference_id", length = 100)
    private String referenceId; // İlgili sipariş/tedarik ID'si

    @Column(name = "performed_by", length = 100)
    private String performedBy; // İşlemi yapan kullanıcı/sistem

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // movement type içibn enum
    public enum MovementType {
        IN,  // Stok girişi (yeni mal alımı, iade vb.)
        OUT  // Stok çıkışı (satış, hasarlı ürün vb.)
    }

    // Constructors
    public StockMovement() {
    }

    public StockMovement(ProductStock productStock, MovementType movementType, Integer quantity,
                        Integer previousQuantity, Integer newQuantity, String reason) {
        this.productStock = productStock;
        this.movementType = movementType;
        this.quantity = quantity;
        this.previousQuantity = previousQuantity;
        this.newQuantity = newQuantity;
        this.reason = reason;
    }

    // Getters ve Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductStock getProductStock() {
        return productStock;
    }

    public void setProductStock(ProductStock productStock) {
        this.productStock = productStock;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPreviousQuantity() {
        return previousQuantity;
    }

    public void setPreviousQuantity(Integer previousQuantity) {
        this.previousQuantity = previousQuantity;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
