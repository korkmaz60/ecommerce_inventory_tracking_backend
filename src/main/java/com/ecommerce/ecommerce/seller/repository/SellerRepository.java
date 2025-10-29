package com.ecommerce.ecommerce.seller.repository;

import com.ecommerce.ecommerce.seller.entity.Seller;
import com.ecommerce.ecommerce.seller.entity.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByUserId(Long userId);

    boolean existsByTaxNumber(String taxNumber);

    boolean existsByIban(String iban);

    List<Seller> findByStatus(SellerStatus status);

    List<Seller> findByActiveTrue();
}
