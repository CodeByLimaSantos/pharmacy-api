package com.limasantos.pharmacy.api.supplier.dto;

import com.limasantos.pharmacy.api.category.entity.ProductCategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SupplierProductDTO {
    private Long productId;
    private String productName;
    private String productCode;
    private ProductCategoryType category;
    private BigDecimal priceCost;
    private BigDecimal priceSale;
    private Boolean controlled;
}

