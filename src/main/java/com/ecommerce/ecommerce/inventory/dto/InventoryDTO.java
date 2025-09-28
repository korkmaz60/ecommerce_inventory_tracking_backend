package com.ecommerce.ecommerce.inventory.dto;

import lombok.Data;

@Data
public class InventoryDTO {
    private Long id;
    private Long productId;
    private Integer quantity;
    private String location;
    private String status;
}
