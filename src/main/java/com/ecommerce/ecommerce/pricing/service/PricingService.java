package com.ecommerce.ecommerce.pricing.service;

import com.ecommerce.ecommerce.pricing.dto.request.PriceUpdateRequest;
import com.ecommerce.ecommerce.pricing.dto.response.PriceHistoryResponse;
import com.ecommerce.ecommerce.pricing.dto.response.PriceResponse;
import com.ecommerce.ecommerce.pricing.entity.PriceHistory;
import com.ecommerce.ecommerce.pricing.entity.ProductPrice;
import com.ecommerce.ecommerce.pricing.repository.PriceHistoryRepository;
import com.ecommerce.ecommerce.pricing.repository.ProductPriceRepository;
import com.ecommerce.ecommerce.product.entity.Product;
import com.ecommerce.ecommerce.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ecommerce.ecommerce.common.exception.BadRequestException;
import com.ecommerce.ecommerce.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fiyatlandırma işlemlerini yöneten servis sınıfı
 */
@Service
public class PricingService {

    private final ProductPriceRepository productPriceRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final ProductRepository productRepository;

    public PricingService(ProductPriceRepository productPriceRepository,
                         PriceHistoryRepository priceHistoryRepository,
                         ProductRepository productRepository) {
        this.productPriceRepository = productPriceRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.productRepository = productRepository;
    }


    public PriceResponse getPriceByProductId(Long productId) {
        ProductPrice price = productPriceRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Bu ürün için fiyat bilgisi bulunamadı: " + productId));
        return toPriceResponse(price);
    }

    /**
     * Aktif indirimi olan ürünleri getirir
     */
    public List<PriceResponse> getProductsWithActiveDiscount() {
        return productPriceRepository.findProductsWithActiveDiscount().stream()
                .map(this::toPriceResponse)
                .collect(Collectors.toList());
    }

    /**
     * Yeni ürün fiyatı oluşturur (ürün oluşturulduğunda)
     */
    @Transactional
    public PriceResponse createPrice(Long productId, BigDecimal price, String currency) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + productId));

        // Zaten fiyat var mı kontrol eder
        if (productPriceRepository.findByProductId(productId).isPresent()) {
            throw new ResourceNotFoundException("Bu ürün için zaten fiyat bilgisi mevcut: " + productId);
        }

        ProductPrice productPrice = new ProductPrice();
        productPrice.setProduct(product);
        productPrice.setCurrentPrice(price);
        productPrice.setCurrency(currency != null ? currency : "TRY");

        ProductPrice saved = productPriceRepository.save(productPrice);

        // İlk fiyat kaydını geçmişe ekler
        createPriceHistory(saved, BigDecimal.ZERO, price, "İlk fiyat oluşturuldu");

        return toPriceResponse(saved);
    }


    @Transactional
    public PriceResponse updatePrice(Long productId, PriceUpdateRequest request) {
        ProductPrice productPrice = productPriceRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Bu ürün için fiyat bilgisi bulunamadı: " + productId));

        BigDecimal oldPrice = productPrice.getCurrentPrice();

        // Fiyat güncelleme
        if (request.getCurrentPrice() != null && !request.getCurrentPrice().equals(oldPrice)) {
            productPrice.setCurrentPrice(request.getCurrentPrice());

            // Fiyat değişikliğini geçmişe kaydeder
            createPriceHistory(productPrice, oldPrice, request.getCurrentPrice(),
                    request.getChangeReason() != null ? request.getChangeReason() : "Fiyat güncellendi");
        }

        // İndirim fiyatı günceller
        if (request.getDiscountPrice() != null) {
            productPrice.setDiscountPrice(request.getDiscountPrice());
        }

        // İndirim yüzdesi günceller
        if (request.getDiscountPercentage() != null) {
            productPrice.setDiscountPercentage(request.getDiscountPercentage());
        }

        // İndirim tarihleri günceller
        if (request.getDiscountStartDate() != null) {
            productPrice.setDiscountStartDate(request.getDiscountStartDate());
        }

        if (request.getDiscountEndDate() != null) {
            productPrice.setDiscountEndDate(request.getDiscountEndDate());
        }

        // Para birimi günceller
        if (request.getCurrency() != null) {
            productPrice.setCurrency(request.getCurrency());
        }

        ProductPrice updated = productPriceRepository.save(productPrice);
        return toPriceResponse(updated);
    }

    /**
     * İndirim uygulanır
     */
    @Transactional
    public PriceResponse applyDiscount(Long productId, BigDecimal discountPrice,
                                      BigDecimal discountPercentage,
                                      String startDate, String endDate) {
        ProductPrice productPrice = productPriceRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Bu ürün için fiyat bilgisi bulunamadı: " + productId));

        productPrice.setDiscountPrice(discountPrice);
        productPrice.setDiscountPercentage(discountPercentage);
        // TODO: startDate ve endDate'i LocalDateTime'a çevir

        ProductPrice updated = productPriceRepository.save(productPrice);
        return toPriceResponse(updated);
    }

    /**
     * İndirimi kaldırır
     */
    @Transactional
    public PriceResponse removeDiscount(Long productId) {
        ProductPrice productPrice = productPriceRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Bu ürün için fiyat bilgisi bulunamadı: " + productId));

        productPrice.setDiscountPrice(null);
        productPrice.setDiscountPercentage(null);
        productPrice.setDiscountStartDate(null);
        productPrice.setDiscountEndDate(null);

        ProductPrice updated = productPriceRepository.save(productPrice);
        return toPriceResponse(updated);
    }

    /**
     * Fiyat geçmişini getirir
     */
    public Page<PriceHistoryResponse> getPriceHistory(Long productId, Pageable pageable) {
        ProductPrice productPrice = productPriceRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Bu ürün için fiyat bilgisi bulunamadı: " + productId));

        return priceHistoryRepository.findByProductPriceIdOrderByChangedAtDesc(productPrice.getId(), pageable)
                .map(this::toPriceHistoryResponse);
    }

    /**
     * Fiyat geçmiş kaydı oluşturur
     */
    private void createPriceHistory(ProductPrice productPrice, BigDecimal oldPrice,
                                    BigDecimal newPrice, String reason) {
        PriceHistory history = new PriceHistory();
        history.setProductPrice(productPrice);
        history.setOldPrice(oldPrice);
        history.setNewPrice(newPrice);
        history.setChangeReason(reason);
        // TODO: Authentication context'ten kullanıcı bilgisi al
        history.setChangedBy("SYSTEM");

        priceHistoryRepository.save(history);
    }

    /**
     * Entity'yi PriceResponse'a dönüştürür
     */
    private PriceResponse toPriceResponse(ProductPrice productPrice) {
        return PriceResponse.builder()
                .id(productPrice.getId())
                .currentPrice(productPrice.getCurrentPrice())
                .discountPrice(productPrice.getDiscountPrice())
                .discountPercentage(productPrice.getDiscountPercentage())
                .discountStartDate(productPrice.getDiscountStartDate())
                .discountEndDate(productPrice.getDiscountEndDate())
                .currency(productPrice.getCurrency())
                .effectivePrice(productPrice.getEffectivePrice())
                .hasActiveDiscount(productPrice.isDiscountActive())
                .updatedAt(productPrice.getUpdatedAt())
                .build();
    }

    /**
     * Entity'yi PriceHistoryResponse'a dönüştürür
     */
    private PriceHistoryResponse toPriceHistoryResponse(PriceHistory priceHistory) {
        return PriceHistoryResponse.builder()
                .id(priceHistory.getId())
                .oldPrice(priceHistory.getOldPrice())
                .newPrice(priceHistory.getNewPrice())
                .changeReason(priceHistory.getChangeReason())
                .changedBy(priceHistory.getChangedBy())
                .changedAt(priceHistory.getChangedAt())
                .build();
    }
}
