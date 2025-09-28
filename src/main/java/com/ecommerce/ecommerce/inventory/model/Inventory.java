package com.ecommerce.ecommerce.inventory.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Integer quantity;
    private String location;
    private String status;
}
