package com.ecommerce.ecommerce.inventory.controller;

import com.ecommerce.ecommerce.inventory.dto.request.StockMovementRequest;
import com.ecommerce.ecommerce.inventory.dto.request.StockUpdateRequest;
import com.ecommerce.ecommerce.inventory.dto.response.StockResponse;
import com.ecommerce.ecommerce.inventory.entity.StockMovement;
import com.ecommerce.ecommerce.inventory.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<StockResponse> getStockByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getStockByProductId(productId));
    }


    @GetMapping("/in-stock")
    public ResponseEntity<List<StockResponse>> getInStockProducts() {
        return ResponseEntity.ok(inventoryService.getInStockProducts());
    }


    @GetMapping("/out-of-stock")
    public ResponseEntity<List<StockResponse>> getOutOfStockProducts() {
        return ResponseEntity.ok(inventoryService.getOutOfStockProducts());
    }

    /**
     * Kritik stok seviyesindeki ürünleri listeler
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<StockResponse>> getLowStockProducts() {
        return ResponseEntity.ok(inventoryService.getLowStockProducts());
    }


    @PostMapping("/product/{productId}")
    public ResponseEntity<StockResponse> createStock(
            @PathVariable Long productId,
            @RequestParam(required = false, defaultValue = "0") Integer quantity,
            @RequestParam(required = false, defaultValue = "0") Integer minStockLevel) {
        StockResponse created = inventoryService.createStock(productId, quantity, minStockLevel);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Stok bilgilerini günceller (min/max seviye, depo)
     */
    @PutMapping("/product/{productId}")
    public ResponseEntity<StockResponse> updateStockInfo(
            @PathVariable Long productId,
            @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.updateStockInfo(productId, request));
    }

    /**
     * Stok hareketi ekle (giriş/çıkış)
     */
    @PostMapping("/movement")
    public ResponseEntity<StockResponse> addStockMovement(@RequestBody StockMovementRequest request) {
        return ResponseEntity.ok(inventoryService.addStockMovement(request));
    }

    /**
     * Stok hareketlerini listele
     */
    @GetMapping("/product/{productId}/movements")
    public ResponseEntity<Page<StockMovement>> getStockMovements(
            @PathVariable Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getStockMovements(productId, pageable));
    }
}
