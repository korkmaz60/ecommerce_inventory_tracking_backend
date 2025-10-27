package com.ecommerce.ecommerce.inventory.dto.request;

import lombok.Data;


@Data
public class StockUpdateRequest {

    private Integer minStockLevel;

    private Integer maxStockLevel;

    private String warehouseLocation; // Depo lokasyonu
}
