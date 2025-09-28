package com.ecommerce.ecommerce.product.repository;

import com.ecommerce.ecommerce.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom queries can be added here
}
