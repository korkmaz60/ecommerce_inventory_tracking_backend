package com.ecommerce.ecommerce.shipping.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "shipping")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String trackingNumber;
    private String shippingAddress;
    private String status;
    private LocalDateTime shippingDate;
    private LocalDateTime estimatedDeliveryDate;
}
