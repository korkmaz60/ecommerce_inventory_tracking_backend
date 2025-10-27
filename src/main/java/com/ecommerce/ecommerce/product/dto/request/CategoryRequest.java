package com.ecommerce.ecommerce.product.dto.request;

import lombok.Data;

@Data
public class CategoryRequest {

    private String name;

    private String description;

    private String slug;

    private Long parentId;

    private Boolean active;
}
