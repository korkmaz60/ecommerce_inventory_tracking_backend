package com.ecommerce.ecommerce.pricing.controller;

import com.ecommerce.ecommerce.pricing.dto.request.PriceUpdateRequest;
import com.ecommerce.ecommerce.pricing.dto.response.PriceHistoryResponse;
import com.ecommerce.ecommerce.pricing.dto.response.PriceResponse;
import com.ecommerce.ecommerce.pricing.service.PricingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Fiyatlandırma işlemleri için REST API Controller
 */
@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    /**
     * Ürün fiyatını getir
     * GET /api/pricing/product/{productId}
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<PriceResponse> getPriceByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(pricingService.getPriceByProductId(productId));
    }

    /**
     * Aktif indirimi olan ürünleri listele
     * GET /api/pricing/discounts/active
     */
    @GetMapping("/discounts/active")
    public ResponseEntity<List<PriceResponse>> getActiveDiscounts() {
        return ResponseEntity.ok(pricingService.getProductsWithActiveDiscount());
    }

    /**
     * Yeni ürün fiyatı oluştur
     * POST /api/pricing/product/{productId}
     */
    @PostMapping("/product/{productId}")
    public ResponseEntity<PriceResponse> createPrice(
            @PathVariable Long productId,
            @RequestParam BigDecimal price,
            @RequestParam(required = false, defaultValue = "TRY") String currency) {
        PriceResponse created = pricingService.createPrice(productId, price, currency);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Ürün fiyatını güncelle
     * PUT /api/pricing/product/{productId}
     */
    @PutMapping("/product/{productId}")
    public ResponseEntity<PriceResponse> updatePrice(
            @PathVariable Long productId,
            @RequestBody PriceUpdateRequest request) {
        return ResponseEntity.ok(pricingService.updatePrice(productId, request));
    }

    /**
     * İndirimi kaldır
     * DELETE /api/pricing/product/{productId}/discount
     */
    @DeleteMapping("/product/{productId}/discount")
    public ResponseEntity<PriceResponse> removeDiscount(@PathVariable Long productId) {
        return ResponseEntity.ok(pricingService.removeDiscount(productId));
    }

    /**
     * Fiyat geçmişini getir
     * GET /api/pricing/product/{productId}/history
     */
    @GetMapping("/product/{productId}/history")
    public ResponseEntity<Page<PriceHistoryResponse>> getPriceHistory(
            @PathVariable Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(pricingService.getPriceHistory(productId, pageable));
    }
}
