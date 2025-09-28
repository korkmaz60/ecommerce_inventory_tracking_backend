package com.ecommerce.ecommerce.payment.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime paymentDate;
}
