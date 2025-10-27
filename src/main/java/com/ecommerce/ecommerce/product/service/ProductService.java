package com.ecommerce.ecommerce.product.service;

import com.ecommerce.ecommerce.product.dto.request.ProductCreateRequest;
import com.ecommerce.ecommerce.product.dto.request.ProductUpdateRequest;
import com.ecommerce.ecommerce.product.dto.response.ProductResponse;
import com.ecommerce.ecommerce.product.entity.Category;
import com.ecommerce.ecommerce.product.entity.Product;
import com.ecommerce.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.ecommerce.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.ecommerce.common.exception.DuplicateResourceException;
import com.ecommerce.ecommerce.common.exception.ResourceNotFoundException;

import java.math.BigDecimal;

/**
 * Ürün işlemlerini yöneten servis sınıfı
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + id));
        return toResponse(product);
    }


    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + sku));
        return toResponse(product);
    }


    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(this::toResponse);
    }


    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(this::toResponse);
    }

    /**
     * İsme göre ürün arama
     */
    public Page<ProductResponse> searchProductsByName(String keyword, Pageable pageable) {
        return productRepository.searchByName(keyword, pageable)
                .map(this::toResponse);
    }

    /**
     * İsim veya açıklamada arama
     */
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchByNameOrDescription(keyword, pageable)
                .map(this::toResponse);
    }


    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        // SKU kontrolü
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Ürün", "SKU", request.getSku());
        }

        // Kategori kontrolü
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.getActive() != null ? request.getActive() : true);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    /**
     * Ürün güncelle
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + id));

        // İsim güncelleme
        if (request.getName() != null) {
            product.setName(request.getName());
        }

        // Açıklama güncelleme
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        // SKU güncelleme (benzersizlik kontrolü)
        if (request.getSku() != null && !product.getSku().equals(request.getSku())) {
            if (productRepository.existsBySku(request.getSku())) {
                throw new DuplicateResourceException("Ürün", "SKU", request.getSku());
            }
            product.setSku(request.getSku());
        }

        // Kategori güncelleme
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Kategori bulunamadı: " + request.getCategoryId()));
            product.setCategory(category);
        }

        // Resim URL güncelleme
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        // Aktiflik durumu güncelleme
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    /**
     * Ürün silme (soft delete)
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    /**
     * Entity'yi Response'a dönüştürür
     */
    private ProductResponse toResponse(Product product) {
        // Fiyat bilgisi (ProductPrice'dan)
        BigDecimal price = null;
        BigDecimal discountPrice = null;
        String currency = null;

        if (product.getProductPrice() != null) {
            price = product.getProductPrice().getCurrentPrice();
            discountPrice = product.getProductPrice().getDiscountPrice();
            currency = product.getProductPrice().getCurrency();
        }

        // Stok bilgisi (ProductStock'tan)
        Integer stock = null;
        Boolean inStock = null;

        if (product.getProductStock() != null) {
            stock = product.getProductStock().getQuantity();
            inStock = product.getProductStock().getInStock();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .imageUrl(product.getImageUrl())
                .price(price)
                .discountPrice(discountPrice)
                .currency(currency)
                .stock(stock)
                .inStock(inStock)
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}

