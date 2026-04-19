package com.limasantos.pharmacy.api.dto.response.domain.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockResponse {
    private Long productId;
    private Integer stock;
}

