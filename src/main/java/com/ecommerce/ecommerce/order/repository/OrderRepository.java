package com.ecommerce.ecommerce.order.repository;

import com.ecommerce.ecommerce.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    java.util.List<Order> findByUserId(Long userId);
}
