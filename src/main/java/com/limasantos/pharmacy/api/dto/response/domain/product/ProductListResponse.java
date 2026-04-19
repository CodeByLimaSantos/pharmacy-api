package com.limasantos.pharmacy.api.dto.response.domain.product;

import com.limasantos.pharmacy.api.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    private Long id;
    private String name;
    private BigDecimal priceSale;
    private Boolean controlled;

    public static ProductListResponse fromDto(ProductDTO dto) {
        return new ProductListResponse(dto.getId(), dto.getName(), dto.getPriceSale(), dto.getControlled());
    }
}

