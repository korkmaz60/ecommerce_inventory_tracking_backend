package com.ecommerce.ecommerce.inventory.service;

import com.ecommerce.ecommerce.inventory.dto.request.StockMovementRequest;
import com.ecommerce.ecommerce.inventory.dto.request.StockUpdateRequest;
import com.ecommerce.ecommerce.inventory.dto.response.StockResponse;
import com.ecommerce.ecommerce.inventory.entity.ProductStock;
import com.ecommerce.ecommerce.inventory.entity.StockMovement;
import com.ecommerce.ecommerce.inventory.repository.ProductStockRepository;
import com.ecommerce.ecommerce.inventory.repository.StockMovementRepository;
import com.ecommerce.ecommerce.product.entity.Product;
import com.ecommerce.ecommerce.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ecommerce.ecommerce.common.exception.BadRequestException;
import com.ecommerce.ecommerce.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Stok yönetimi işlemlerini yöneten servis sınıfı
 */
@Service
public class InventoryService {

    private final ProductStockRepository productStockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    public InventoryService(ProductStockRepository productStockRepository,
                           StockMovementRepository stockMovementRepository,
                           ProductRepository productRepository) {
        this.productStockRepository = productStockRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
    }


    public StockResponse getStockByProductId(Long productId) {
        ProductStock stock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Bu ürün için stok bilgisi bulunamadı: " + productId));
        return toStockResponse(stock);
    }


    public List<StockResponse> getInStockProducts() {
        return productStockRepository.findByInStockTrue().stream()
                .map(this::toStockResponse)
                .collect(Collectors.toList());
    }


    public List<StockResponse> getOutOfStockProducts() {
        return productStockRepository.findByInStockFalse().stream()
                .map(this::toStockResponse)
                .collect(Collectors.toList());
    }

    /**
     * Kritik stok seviyesindeki ürünleri listeler
     */
    public List<StockResponse> getLowStockProducts() {
        return productStockRepository.findLowStockProducts().stream()
                .map(this::toStockResponse)
                .collect(Collectors.toList());
    }

    /**
     * Yeni ürün stoğu oluşturur (ürün oluşturulduğunda)
     */
    @Transactional
    public StockResponse createStock(Long productId, Integer quantity, Integer minStockLevel) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + productId));

        // Zaten stok var mı kontrol et
        if (productStockRepository.findByProductId(productId).isPresent()) {
            throw new ResourceNotFoundException("Bu ürün için zaten stok bilgisi mevcut: " + productId);
        }

        ProductStock productStock = new ProductStock();
        productStock.setProduct(product);
        productStock.setQuantity(quantity != null ? quantity : 0);
        productStock.setMinStockLevel(minStockLevel != null ? minStockLevel : 0);

        ProductStock saved = productStockRepository.save(productStock);

        // İlk stok kaydını hareket tablosuna ekle
        if (quantity != null && quantity > 0) {
            createStockMovement(saved, StockMovement.MovementType.IN, quantity, 0, quantity, "İlk stok girişi");
        }

        return toStockResponse(saved);
    }

    /**
     * Stok bilgilerini günceller (min/max seviye, depo lokasyonu)
     */
    @Transactional
    public StockResponse updateStockInfo(Long productId, StockUpdateRequest request) {
        ProductStock stock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Bu ürün için stok bilgisi bulunamadı: " + productId));

        if (request.getMinStockLevel() != null) {
            stock.setMinStockLevel(request.getMinStockLevel());
        }

        if (request.getMaxStockLevel() != null) {
            stock.setMaxStockLevel(request.getMaxStockLevel());
        }

        if (request.getWarehouseLocation() != null) {
            stock.setWarehouseLocation(request.getWarehouseLocation());
        }

        ProductStock updated = productStockRepository.save(stock);
        return toStockResponse(updated);
    }

    /**
     * Stok hareketi oluşturur (giriş/çıkış)
     */
    @Transactional
    public StockResponse addStockMovement(StockMovementRequest request) {
        ProductStock stock = productStockRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Bu ürün için stok bilgisi bulunamadı: " + request.getProductId()));

        Integer previousQuantity = stock.getQuantity();
        Integer newQuantity;

        // Hareket tipine göre stok güncelle
        if (request.getMovementType() == StockMovement.MovementType.IN) {
            // Stok girişi
            stock.addStock(request.getQuantity());
            newQuantity = stock.getQuantity();
        } else {
            // Stok çıkışı
            try {
                stock.reduceStock(request.getQuantity());
                newQuantity = stock.getQuantity();
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Yetersiz stok! Mevcut: " + stock.getQuantity() + ", İstenen: " + request.getQuantity());
            }
        }

        // Hareketi kaydet
        createStockMovement(stock, request.getMovementType(), request.getQuantity(),
                previousQuantity, newQuantity, request.getReason());

        // Stok kaydını güncelle
        ProductStock updated = productStockRepository.save(stock);
        return toStockResponse(updated);
    }

    /**
     * Stok hareketlerini getir
     */
    public Page<StockMovement> getStockMovements(Long productId, Pageable pageable) {
        ProductStock stock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Bu ürün için stok bilgisi bulunamadı: " + productId));

        return stockMovementRepository.findByProductStockIdOrderByCreatedAtDesc(stock.getId(), pageable);
    }

    /**
     * Stok hareketi kaydet
     */
    private void createStockMovement(ProductStock stock, StockMovement.MovementType movementType,
                                     Integer quantity, Integer previousQuantity, Integer newQuantity, String reason) {
        StockMovement movement = new StockMovement();
        movement.setProductStock(stock);
        movement.setMovementType(movementType);
        movement.setQuantity(quantity);
        movement.setPreviousQuantity(previousQuantity);
        movement.setNewQuantity(newQuantity);
        movement.setReason(reason != null ? reason : movementType.name());
        // TODO: Authentication context'ten kullanıcı bilgisi al
        movement.setPerformedBy("SYSTEM");

        stockMovementRepository.save(movement);
    }

    /**
     * Entity'yi StockResponse'a dönüştürür
     */
    private StockResponse toStockResponse(ProductStock stock) {
        return StockResponse.builder()
                .id(stock.getId())
                .quantity(stock.getQuantity())
                .minStockLevel(stock.getMinStockLevel())
                .maxStockLevel(stock.getMaxStockLevel())
                .warehouseLocation(stock.getWarehouseLocation())
                .inStock(stock.getInStock())
                .isLowStock(stock.isLowStock())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
}
