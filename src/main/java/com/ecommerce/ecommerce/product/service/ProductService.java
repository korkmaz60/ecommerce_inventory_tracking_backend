package com.ecommerce.ecommerce.product.service;

import com.ecommerce.ecommerce.product.dto.ProductDTO;
import com.ecommerce.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductDTO findById(Long id) {
        // Implementation will be added
        return null;
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        // Implementation will be added
        return null;
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        // Implementation will be added
        return null;
    }

    public void deleteProduct(Long id) {
        // Implementation will be added
    }
}
